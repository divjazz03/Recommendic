package com.divjazz.recommendic.user.controller.patient.payload;

import com.divjazz.recommendic.global.validation.annotation.ValidEnum;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.validation.ValidDateOfBirth;
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
        @NotNull(message = "Date of birth is required")
        @NotBlank(message = "Date of birth cannot be blank")
        @ValidDateOfBirth
        String dateOfBirth,
        @NotNull(message = "gender is required")
        @ValidEnum(enumClass = Gender.class)
        String gender) {
}

