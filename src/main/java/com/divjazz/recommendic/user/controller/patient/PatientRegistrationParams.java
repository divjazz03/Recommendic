package com.divjazz.recommendic.user.controller.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;


@JsonIgnoreProperties(ignoreUnknown = true)
public record PatientRegistrationParams(
        @NotEmpty(message = "First name cannot be empty or null")
        String firstName,
        @NotEmpty(message = "Last name cannot be empty or null")
        String lastName,
        @NotEmpty(message = "Email cannot be empty or null")
        @Email(message = "Invalid email address")
        String email,
        @NotEmpty(message = "Password cannot be empty or null")
        String password,
        @NotEmpty(message = "Phone number cannot be empty or null")
        String phoneNumber,
        @NotEmpty(message = "Gender cannot be empty")
        String gender,
        @NotEmpty(message = "City cannot be empty")
        String city,
        @NotEmpty(message = "State cannot be empty")
        String state,
        @NotEmpty(message = "Country cannot be empty")
        String country) {
}

