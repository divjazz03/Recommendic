package com.divjazz.recommendic.user.controller.patient.payload;

public record MedicalHistoryDTO(
        String knownAllergies,
        String chronicConditions,
        String currentMedications,
        String pastSurgeries,
        String familyMedicalHistory
) {
}
