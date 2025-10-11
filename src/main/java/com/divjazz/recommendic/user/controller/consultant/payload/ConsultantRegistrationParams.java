package com.divjazz.recommendic.user.controller.consultant.payload;

import com.divjazz.recommendic.global.validation.annotation.Gender;
import com.divjazz.recommendic.user.validation.ValidDateOfBirth;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
        @NotEmpty(message = "Date of birth cannot be empty or null")
        @NotBlank(message = "Date of birth should not be blank")
        @ValidDateOfBirth
        @Schema(name = "Date Of Birth", example = "13-04-2004", requiredMode = Schema.RequiredMode.REQUIRED)
        String dateOfBirth,
        @Gender
        @NotEmpty(message = "Gender cannot be empty or null")
        @Schema(name = "Gender", example = "MALE | FEMALE", requiredMode = Schema.RequiredMode.REQUIRED)
        String gender){
}
