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

/**
 * ShiroRoles generated by hbm2java
 */
@Entity
@Table(name = "shiro_roles", catalog = "authmsme"
)
public class ShiroRoles implements java.io.Serializable {

    private String name;
    private String description;
    private Set shiroUserses = new HashSet(0);
    private Set shiroPermissionses = new HashSet(0);

    public ShiroRoles() {
    }

    public ShiroRoles(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ShiroRoles(String name, String description, Set shiroUserses, Set shiroPermissionses) {
        this.name = name;
        this.description = description;
        this.shiroUserses = shiroUserses;
        this.shiroPermissionses = shiroPermissionses;
    }

    @Id
    @Column(name = "name", unique = true, nullable = false, length = 30)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", nullable = false, length = 100)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "shiro_users_roles", catalog = "authmsme", joinColumns = {
        @JoinColumn(name = "role", nullable = false, updatable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "user_name", nullable = false, updatable = false)})
    public Set getShiroUserses() {
        return this.shiroUserses;
    }

    public void setShiroUserses(Set shiroUserses) {
        this.shiroUserses = shiroUserses;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "shiro_roles_permissions", catalog = "authmsme", joinColumns = {
        @JoinColumn(name = "role", nullable = false, updatable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "permission", nullable = false, updatable = false)})
    public Set getShiroPermissionses() {
        return this.shiroPermissionses;
    }

    public void setShiroPermissionses(Set shiroPermissionses) {
        this.shiroPermissionses = shiroPermissionses;
    }

}
