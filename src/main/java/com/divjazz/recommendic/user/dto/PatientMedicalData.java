package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.enums.Gender;

public record PatientMedicalData(
        String id,
        String consultationId,
        String name,
        String age,
        Gender gender,
        String mrn
) {
}
