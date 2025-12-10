package com.divjazz.recommendic.user.controller.patient.payload;

public record LifeStyleInfoDTO(
        String smokingStatus,
        String alcoholConsumption,
        String exerciseFrequency,
        String dietaryRestriction
) {
}
