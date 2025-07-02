package com.divjazz.recommendic.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


public record LoginRequest(
        @NotNull(message = "Email is required")
        @Email(message = "Invalid email address")
        String email,
        @NotEmpty(message = "Password cannot be empty")
        @NotNull(message = "Password is required")
        String password
) {
}
