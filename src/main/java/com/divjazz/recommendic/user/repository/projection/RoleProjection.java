package com.divjazz.recommendic.user.repository.projection;

import com.divjazz.recommendic.user.model.userAttributes.Role;

public interface RoleProjection {
    String getName();
    String getPermissions();

    default Role toRole() {
        return new Role(getName(),getPermissions());
    }
}
