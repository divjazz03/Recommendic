package com.divjazz.recommendic.user.controller.admin;

import jakarta.validation.constraints.NotEmpty;

public record AdminRegistrationParams(
        @NotEmpty(message = "First name cannot be empty or null")
        String firstName,
        @NotEmpty(message = "last name cannot be empty or null")
        String lastName,
        @NotEmpty(message = "Email cannot be empty or null")
        String email,
        @NotEmpty(message = "Phone number cannot be empty or null")
        String phoneNumber,
        @NotEmpty(message = "Gender cannot be empty or null")
        String gender,
        @NotEmpty(message = "zipcode cannot be empty or null")
        String zipcode,
        @NotEmpty(message = "City cannot be empty or null")
        String city,
        @NotEmpty(message = "State cannot be empty or null")
        String state,
        @NotEmpty(message = "Country cannot be empty or null")
        String country
){}
