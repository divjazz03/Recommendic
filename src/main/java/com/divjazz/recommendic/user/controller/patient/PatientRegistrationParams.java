package com.divjazz.recommendic.user.controller.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;


@JsonIgnoreProperties(ignoreUnknown = true)
public record PatientRegistrationParams(
        @NotNull(message = "firstname is required")
        @NotBlank(message = "firstname cannot be blank")
        String firstName,
        @NotNull(message = "lastname is required")
        @NotBlank(message = "First name cannot be blank")
        String lastName,
        @NotNull(message = "email is required")
        @NotBlank(message = "email cannot be blank")
        @Email(message = "Invalid email address")
        String email,
        @Min(value = 8, message = "password must be greater than 8 characters")
        @NotNull(message = "password is required")
        @NotBlank(message = "password cannot be blank")
        String password,
        @NotNull(message = "phone number is required")
        @NotBlank(message = "phone number cannot be blank")
        String phoneNumber,
        @NotNull(message = "gender is required")
        @NotBlank(message = "gender cannot be blank")
        String gender,
        @NotNull(message = "city is required")
        @NotBlank(message = "city cannot be blank")
        String city,
        @NotNull(message = "state is required")
        @NotBlank(message = "state cannot be blank")
        String state,
        @NotNull(message = "country is required")
        @NotBlank(message = "country cannot be blank")
        String country) {
}

