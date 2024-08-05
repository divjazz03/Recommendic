package com.divjazz.recommendic.user.model.userAttributes;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.enums.Authority;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@Table(name = "roles")
@JsonInclude(NON_DEFAULT)
public class Role{

    @Column(name = "name")
    private String name;
    @Column(name = "permissions")
    private Authority permissions;

    @Id
    private Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Authority getPermissions() {
        return permissions;
    }

    public void setPermissions(Authority permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", permissions='" + permissions + '\'' +
                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
