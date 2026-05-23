package com.divjazz.recommendic.user.controller.consultant.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record ProfileInformationRequest(
        @NotBlank(message = "Containing whitespace alone not allowed")
        @Size(max = 500, message = "Bio should not be more than 500 characters")
        String bio,
        @NotNull(message = "Profile picture is required")
        @URL(message = "Invalid URL")
        String profilePictureUrl

        ) implements ConsultantOnboardingRequest{
}
