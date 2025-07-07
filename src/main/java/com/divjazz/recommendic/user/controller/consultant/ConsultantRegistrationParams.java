package com.divjazz.recommendic.user.controller.consultant;

import com.divjazz.recommendic.global.validation.annotations.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record ConsultantRegistrationParams(
        @NotEmpty(message = "First name cannot be empty or null")
        @Schema(name = "First Name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,

        @Schema(name = "Last Name", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "Last name cannot be empty or null")
        String lastName,
        @NotEmpty(message = "Email cannot be empty or null")
        @Email()
        @Schema(name = "Email", example = "JohnDoe@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @NotEmpty(message = "Password cannot be empty or null")
        @Schema(name = "Password", example = "your secure password", requiredMode = Schema.RequiredMode.REQUIRED)
        String password,
        @NotEmpty(message = "Phone number cannot be empty or null")
        @Schema(name = "Phone Number", example = "+23470467283", requiredMode = Schema.RequiredMode.REQUIRED)
        String phoneNumber,
        @Gender
        @NotEmpty(message = "Gender cannot be empty or null")
        @Schema(name = "Gender", example = "MALE | FEMALE", requiredMode = Schema.RequiredMode.REQUIRED)
        String gender,
        @NotEmpty(message = "City cannot be empty or null")
        @Schema(name = "City", example = "PortHarcourt", requiredMode = Schema.RequiredMode.REQUIRED)
        String city,
        @NotEmpty(message = "State cannot be empty or null")
        @Schema(name = "State", example = "Rivers", requiredMode = Schema.RequiredMode.REQUIRED)
        String state,
        @NotEmpty(message = "Country cannot be empty or null")
        @Schema(name = "Country", example = "Nigeria", requiredMode = Schema.RequiredMode.REQUIRED)
        String country
) {
}
