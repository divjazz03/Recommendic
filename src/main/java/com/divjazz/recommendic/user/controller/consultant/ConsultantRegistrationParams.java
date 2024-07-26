package com.divjazz.recommendic.user.controller.consultant;

import jakarta.validation.constraints.NotEmpty;

public record ConsultantRegistrationParams(
        @NotEmpty(message = "First name cannot be empty or null")
        String firstName,
        @NotEmpty(message = "Last name cannot be empty or null")
        String lastName,
        @NotEmpty(message = "Email cannot be empty or null")
        String email,
        @NotEmpty(message = "Password cannot be empty or null")
        String password,
        @NotEmpty(message = "Phone number cannot be empty or null")
        String phoneNumber,
        @NotEmpty(message = "Gender cannot be empty or null")
        String gender,
        @NotEmpty(message = "Zip Code cannot be empty or null")
        String zipCode,
        @NotEmpty(message = "City cannot be empty or null")
        String city,
        @NotEmpty(message = "State cannot be empty or null")
        String state,
        @NotEmpty(message = "Country cannot be empty or null")
        String country,
        @NotEmpty(message = "Medical Category cannot be empty or null")
        String medicalSpecialization
) {}
