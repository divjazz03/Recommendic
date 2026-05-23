package com.divjazz.recommendic.user.controller.consultant.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.Set;

public record PracticeDetails(
        @Max(value = 50000L, message = "Fee should not be more than #50000")
        @Min(value = 1000L, message = "Fee should not be less than #1000")
        Integer consultationFee,
        @Max(value = 120, message = "Consultation Duration should not exceed 2 hours")
        @Min(value = 15, message = "Consultation Duration should not be less than 15 minutes")
        Integer consultationDuration,
        Set<String> availableDays,
        Set<String> preferredTimeSlots
) implements ConsultantOnboardingRequest {
}
