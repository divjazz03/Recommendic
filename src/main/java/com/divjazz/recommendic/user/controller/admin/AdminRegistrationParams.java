package com.divjazz.recommendic.user.controller.admin;

import com.divjazz.recommendic.global.validation.annotations.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record AdminRegistrationParams(
        @NotEmpty(message = "First name cannot be empty or null")
        @JsonProperty("first_name")
        String firstName,
        @NotEmpty(message = "last name cannot be empty or null")
        @JsonProperty("last_name")
        String lastName,
        @NotEmpty(message = "Email cannot be empty or null")
        @Email()
        String email,
        @NotEmpty(message = "Phone number cannot be empty or null")
        @JsonProperty("phone_number")
        String phoneNumber,
        @Gender
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
) {
}
