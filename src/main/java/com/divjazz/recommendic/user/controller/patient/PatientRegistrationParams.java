package com.divjazz.recommendic.user.controller.patient;

import com.divjazz.recommendic.global.validation.annotation.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
        @Size(min = 8,message = "Password should not be less than 8 characters")
        @NotNull(message = "password is required")
        @NotBlank(message = "password cannot be blank")
        String password,
        @NotNull(message = "phone number is required")
        @NotBlank(message = "phone number cannot be blank")
        String phoneNumber,
        @Gender
        @NotNull(message = "gender is required")
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

