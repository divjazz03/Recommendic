package com.divjazz.recommendic.user.controller;

import com.divjazz.recommendic.user.model.userAttributes.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

public record UserPrincipalResponse(
        String email,
        Set<String> authorities,
        Role role
) {
}
