package com.divjazz.recommendic.user.dto;

import java.util.UUID;

public record UserDTO(
        Long createdBy,
        Long updatedBy,
        String userId,
        String firstName,
        String lastName,
        String imageUrl,
        String imageName,
        String lastLogin,
        String createdAt,
        String updatedAt,
        String role,
        boolean accountNonExpired,
        boolean enabled
) {
}

