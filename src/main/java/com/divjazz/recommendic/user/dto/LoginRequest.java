package com.divjazz.recommendic.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;

@Data
public class LoginRequest {
    @NotEmpty(message = "Email cannot be empty or null")
    @Email(message = "Invalid email address")
    private String email;
    @NotEmpty(message = "Password cannot be empty or null")
    private String password;


}
