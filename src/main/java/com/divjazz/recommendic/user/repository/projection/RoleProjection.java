package com.divjazz.recommendic.user.repository.projection;

import com.divjazz.recommendic.user.model.userAttributes.Role;

import java.util.Set;

public interface RoleProjection {
    String getName();
    default Role toRole() {
        return new Role(getName(), null);
    }
}
