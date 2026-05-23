package com.divjazz.recommendic.user.controller.consultant.payload;

import com.divjazz.recommendic.user.dto.CertificationDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record QualificationsRequest(
        @NotNull(message = "Medical degree is required")
        @NotBlank(message = "Containing whitespace alone not allowed")
        String medicalDegree,
        @NotNull(message = "University is required")
        @NotBlank(message = "Containing whitespace alone not allowed")
        String university,
        @NotNull(message = "Graduation year is required")
        Integer graduationYear,
        @Size(max = 50, message = "Maximum number of characters is 50")
        @NotBlank(message = "Containing whitespace alone not allowed")
        String certifications,
        @Size(min = 1, max = 3, message = "Should provide at least 1 credential document and at most 3")
        Set<CertificationDTO> credentials,
        @NotNull(message = "Resume is required")
        CertificationDTO resume

) implements ConsultantOnboardingRequest {
}
