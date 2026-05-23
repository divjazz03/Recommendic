package com.divjazz.recommendic.user.controller.consultant.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ProfessionalInfoRequest(
        @NotNull(message = "Specialization is required")
        @NotBlank(message = "Containing whitespace alone not allowed")
        String specialization,
        @Size(max = 3, message = "Maximum of three subspecialties are allowed")
        Set<String> subSpecialties,
        @NotNull(message = "License number is required")
        @NotBlank(message = "Containing whitespace alone not allowed")
        String licenseNumber,
        @NotNull(message = "Years of experience is required")
        @Min(value = 5L, message = "You must have at least 5 years experience to be eligible")
        Integer yearsOfExperience,
        @NotNull(message = "Work place address is required")
        @NotBlank(message = "Containing whitespace alone not allowed")
        String currentWorkplace,
        @Size(min = 1, max = 5, message = "Should know at least 1 message and at most 5")
        Set<String> languages
) implements ConsultantOnboardingRequest {
}
