package com.divjazz.recommendic.user.controller.consultant.payload;

import com.divjazz.recommendic.user.dto.CertificationDTO;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

@Builder
public record ConsultantOnboardingRequest(
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
        @Size(min = 1, max = 5, message = "Should know at least 1 message and at most 5")
        Set<String> languages,
        @Max(value = 50000L, message = "Fee should not be more than #50000")
        @Min(value = 1000L, message = "Fee should not be less than #1000")
        Integer consultationFee,
        @Max(value = 120, message = "Consultation Duration should not exceed 2 hours")
        @Min(value = 15, message = "Consultation Duration should not be less than 15 minutes")
        Integer consultationDuration,
        Set<String> availableDays,
        Set<String> preferredTimeSlots,
        @NotBlank(message = "Containing whitespace alone not allowed")
        @Size(max = 500, message = "Bio should not be more than 500 characters")
        String bio,
        @NotNull(message = "Profile picture is required")
        @URL(message = "Invalid URL")
        String profilePictureUrl,
        @Size(min = 1, max = 3, message = "Should provide at least 1 credential document and at most 3")
        Set<CertificationDTO> credentials,
        @NotNull(message = "Resume is required")
        CertificationDTO resume
) {
}
