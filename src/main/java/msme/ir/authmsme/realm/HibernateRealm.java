/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 * Realm that allows authentication and authorization via Hibernate Dao calls.  
 * <p/>
 * If the default implementation of authentication and authorization cannot
 * handle your schema, this class can be subclassed and the appropriate methods
 * overridden. (usually {@link #doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)},
 * {@link #getRoleNamesForUser(String)}, and/or
 * {@link #getPermissions(String,java.util.Collection)}
 * <p/>
 * This realm supports caching by extending from
 * {@link org.apache.shiro.realm.AuthorizingRealm}.
 *
 * @since 0.2
 */
package msme.ir.authmsme.realm;

import msme.ir.authmsme.dao.UserDao;
import msme.ir.authmsme.entity.ShiroUser;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import msme.ir.authmsme.dao.PermissionDao;
import msme.ir.authmsme.dao.RoleDao;
import msme.ir.authmsme.entity.ShiroPermission;
import msme.ir.authmsme.entity.ShiroRole;

/**
 *
 * @author jeus
 */
public class HibernateRealm extends JdbcRealm {

    private static final Logger log = LoggerFactory.getLogger(HibernateRealm.class);

    /**
     * Password hash salt configuration. <ul>
     * <li>NO_SALT - password hashes are not salted.</li>
     * <li>CRYPT - password hashes are stored in unix crypt format.</li>
     * <li>COLUMN - salt is in a separate column in the database.</li>
     * <li>EXTERNAL - salt is not stored in the database.
     * {@link #getSaltForUser(String)} will be called to get the salt</li></ul>
     */
    public enum SaltStyle {
        NO_SALT, CRYPT, COLUMN, EXTERNAL
    };

    /*--------------------------------------------
    |    I N S T A N C E   V A R I A B L E S    |
    ============================================*/
    protected SaltStyle saltStyle = SaltStyle.NO_SALT;

    /*--------------------------------------------
    |         C O N S T R U C T O R S           |
    ============================================*/

 /*--------------------------------------------
    |  A C C E S S O R S / M O D I F I E R S    |
    ============================================*/
    /**
     * Sets the datasource that should be used to retrieve connections used by
     * this realm.
     *
     * @param dataSource the SQL data source.
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Overrides the default query used to retrieve a user's password during
     * authentication. When using the default implementation, this query must
     * take the user's username as a single parameter and return a single result
     * with the user's password as the first column. If you require a solution
     * that does not match this query structure, you can override
     * {@link #doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)}
     * or just {@link #getPasswordForUser(java.sql.Connection,String)}
     *
     * @param authenticationQuery the query to use for authentication.
     * @see #DEFAULT_AUTHENTICATION_QUERY
     */
    public void setAuthenticationQuery(String authenticationQuery) {
        this.authenticationQuery = authenticationQuery;
    }

    /**
     * Overrides the default query used to retrieve a user's roles during
     * authorization. When using the default implementation, this query must
     * take the user's username as a single parameter and return a row per role
     * with a single column containing the role name. If you require a solution
     * that does not match this query structure, you can override
     * {@link #doGetAuthorizationInfo(PrincipalCollection)} or just
     * {@link #getRoleNamesForUser(java.sql.Connection,String)}
     *
     * @param userRolesQuery the query to use for retrieving a user's roles.
     * @see #DEFAULT_USER_ROLES_QUERY
     */
    public void setUserRolesQuery(String userRolesQuery) {
        this.userRolesQuery = userRolesQuery;
    }

    /**
     * Enables lookup of permissions during authorization. The default is
     * "false" - meaning that only roles are associated with a user. Set this to
     * true in order to lookup roles <b>and</b> permissions.
     *
     * @param permissionsLookupEnabled true if permissions should be looked up
     * during authorization, or false if only roles should be looked up.
     */
    public void setPermissionsLookupEnabled(boolean permissionsLookupEnabled) {
        this.permissionsLookupEnabled = permissionsLookupEnabled;
    }

    /**
     * Sets the salt style. See {@link #saltStyle}.
     *
     * @param saltStyle new SaltStyle to set.
     */
    public void setSaltStyle(SaltStyle saltStyle) {
        this.saltStyle = saltStyle;
        if (saltStyle == SaltStyle.COLUMN && authenticationQuery.equals(DEFAULT_AUTHENTICATION_QUERY)) {
            authenticationQuery = DEFAULT_SALTED_AUTHENTICATION_QUERY;
        }
    }

    /*--------------------------------------------
    |               M E T H O D S               |
    ============================================*/
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();

        // Null username is invalid
        if (username == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }

        Connection conn = null;
        SimpleAuthenticationInfo info = null;
        try {
            conn = dataSource.getConnection();

            String password = null;
            String salt = null;
            switch (saltStyle) {
                case NO_SALT:
                    password = getPasswordForUser(username)[0];
                    break;
                case CRYPT:
                    // TODO: separate password and hash from getPasswordForUser[0]
                    throw new ConfigurationException("Not implemented yet");
                //break;
                case COLUMN:
                    String[] queryResults = getPasswordForUser(username);
                    password = queryResults[0];
                    salt = queryResults[1];
                    break;
                case EXTERNAL:
                    password = getPasswordForUser(username)[0];
                    salt = getSaltForUser(username);
            }

            if (password == null) {
                throw new UnknownAccountException("No account found for user [" + username + "]");
            }

            info = new SimpleAuthenticationInfo(username, password.toCharArray(), getName());

            if (salt != null) {
                info.setCredentialsSalt(ByteSource.Util.bytes(salt));
            }

        } catch (SQLException e) {
            final String message = "There was a SQL error while authenticating user [" + username + "]";
            if (log.isErrorEnabled()) {
                log.error(message, e);
            }

            // Rethrow any SQL errors as an authentication exception
            throw new AuthenticationException(message, e);
        } finally {
            JdbcUtils.closeConnection(conn);
        }

        return info;
    }

    private String[] getPasswordForUser(String username) throws SQLException {

        String[] result;
        boolean returningSeparatedSalt = false;
        switch (saltStyle) {
            case NO_SALT:
            case CRYPT:
            case EXTERNAL:
                result = new String[1];
                break;
            default:
                result = new String[2];
                returningSeparatedSalt = true;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            UserDao userDao = new UserDao();
            ShiroUser shiroUser = null;

            //Add the user object to the session
            if ((shiroUser = userDao.getUser(username)) == null) {
                result[0] = shiroUser.getPassword();
                if (returningSeparatedSalt) {
                    result[1] = shiroUser.getSalt();
                }
            }
        } catch (Exception er) {
        }
        return result;
    }

    /**
     * This implementation of the interface expects the principals collection to
     * return a String username keyed off of this realm's
     * {@link #getName() name}
     *
     * @see #getAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        String username = (String) getAvailablePrincipal(principals);

        Set<String> roleNames = null;
        Set<String> permissions = null;
        try {
            // Retrieve roles and permissions from database
            roleNames = getRoleNamesForUser(username);
            if (permissionsLookupEnabled) {
                permissions = getPermissions(username, roleNames);
            }

        } catch (Exception e) {
            final String message = "There was a SQL error while authorizing user [" + username + "]";
            if (log.isErrorEnabled()) {
                log.error(message, e);
            }

            // Rethrow any SQL errors as an authorization exception
            throw new AuthorizationException(message, e);
        } finally {

        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
        info.setStringPermissions(permissions);
        return info;

    }

    protected Set<String> getRoleNamesForUser(String username) {

        Set<String> roleNames = new LinkedHashSet<String>();
        try {

            RoleDao roleDao = new RoleDao();
            List<ShiroRole> roles = new ArrayList<>();
            roles = roleDao.getRoles(username);
            // Loop over results and add each returned role to a set
            Iterator<ShiroRole> it = roles.iterator();
            while (it.hasNext()) {
                String roleName = it.next().getName();
                // Add the role to the list of names if it isn't null
                if (roleName != null) {
                    roleNames.add(roleName);
                } else if (log.isWarnEnabled()) {
                    log.warn("Null role name found while retrieving role names for user [" + username + "]");
                }
            }
        } catch (Exception e) {
            log.error("Get error in loading roles " + e.getMessage());
        }
        return roleNames;
    }

    protected Set<String> getPermissions(String username, Collection<String> roleNames) throws SQLException {
        PreparedStatement ps = null;
        Set<String> permissions = new LinkedHashSet<String>();
        try {
            PermissionDao permDao = new PermissionDao();
            List<ShiroPermission> shiroPermissions = new ArrayList<>();
            for (String roleName : roleNames) {

                shiroPermissions = permDao.getPermissions(roleName);
                Iterator<ShiroPermission> it = shiroPermissions.iterator();
                // Loop over results and add each returned role to a set
                while (it.hasNext()) {
                    String permissionString = it.next().getName();
                    // Add the permission to the set of permissions
                    permissions.add(permissionString);
                }

            }
        } catch (Exception e) {
            log.error("Get error in loading permissions " + e.getMessage());
        }
        return permissions;
    }

    protected String getSaltForUser(String username) {
        return username;
    }

}
