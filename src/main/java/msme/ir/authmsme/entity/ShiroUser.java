package msme.ir.authmsme.entity;
// Generated Nov 30, 2016 12:43:01 PM by Hibernate Tools 4.3.1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;

/**
 * ShiroUsers generated by hbm2java
 */
@Entity
@Table(name = "shiro_user", catalog = "authmsme", uniqueConstraints = @UniqueConstraint(columnNames = "user_name")
)
public class ShiroUser implements java.io.Serializable {

    private String personalNumber;
    private String userName;
    private String password;
    private String salt;
    private boolean active;
    private Set shiroRoles = new HashSet(0);

    public ShiroUser() {
    }

    public ShiroUser(String personalNumber, String userName, String password, String salt, boolean active) {
        this.personalNumber = personalNumber;
        this.userName = userName;
        this.password = password;
        this.salt = salt;
        this.active = active;
    }

    public ShiroUser(String personalNumber, String userName, String password, String salt, boolean active, Set shiroRoles) {
        this.personalNumber = personalNumber;
        this.userName = userName;
        this.password = password;
        this.salt = salt;
        this.active = active;
        this.shiroRoles = shiroRoles;
    }

    @Id
    @Column(name = "personal_number", unique = true, nullable = false, length = 45)
    public String getPersonalNumber() {
        return this.personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    @Column(name = "user_name", unique = true, nullable = false, length = 20)
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "password", nullable = false, length = 100)
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        PasswordService passwordService = new DefaultPasswordService();
        this.password = passwordService.encryptPassword(password);
    }

    @Column(name = "salt", nullable = false, length = 24)
    public String getSalt() {
        return this.salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Column(name = "active", nullable = false)
    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "shiro_user_role", catalog = "authmsme", joinColumns = {
        @JoinColumn(name = "user_name", nullable = false, updatable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "role", nullable = false, updatable = false)})
    public Set getShiroRoles() {
        return this.shiroRoles;
    }

    public void setShiroRoles(Set shiroRoles) {
        this.shiroRoles = shiroRoles;
    }

}
