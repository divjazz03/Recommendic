package com.divjazz.recommendic.user.model.userAttributes;


import com.divjazz.recommendic.user.repository.projection.RoleProjection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Entity
@Table(name = "role")
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "permissions")
    private String permissions;

    public Role(String name, String permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public static Role fromProjection(RoleProjection projection) {
        return new Role(projection.getName(),projection.getPermissions());
    }

}
