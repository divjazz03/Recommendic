package com.divjazz.recommendic.user.controller.patient.payload;

import java.util.List;

public record MedicalHistoryDTO(
        List<String> knownAllergies,
        List<String> chronicConditions,
        List<String> currentMedications,
        List<String> pastSurgeries,
        String familyMedicalHistory
) {
}
