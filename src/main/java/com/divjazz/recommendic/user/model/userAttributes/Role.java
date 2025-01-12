package com.divjazz.recommendic.user.model.userAttributes;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@Table(name = "roles")
@JsonInclude(NON_DEFAULT)
public class Role {

    @Column(name = "name")
    private String name;
    @Column(name = "permissions")
    private String permissions;

    @Id
    private Long id;

    protected Role() {
    }

    public Role(String name, String permissions, Long id) {
        this.name = name;
        this.permissions = permissions;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", permissions='" + permissions + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
