package com.divjazz.recommendic.user.model.userAttributes;


import com.divjazz.recommendic.user.repository.projection.RoleProjection;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Getter
@Entity
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "roles_permissions",
            inverseJoinColumns = @JoinColumn(name = "permission_id"),
            joinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Permission> permissions;

    public Role(String name, Set<Permission> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public static Role fromProjection(RoleProjection projection) {
        return new Role(projection.getName(),Set.of());//projection.getPermissions());
    }

}
