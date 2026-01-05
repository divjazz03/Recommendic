package com.divjazz.recommendic.medication.dto;

import com.divjazz.recommendic.medication.constants.MedicationStatus;

public record MedicationDTO (
        String id,
        String name,
        String dosage,
        String frequency,
        String startDate,
        String endDate,
        String instructions,
        MedicationStatus medicationStatus,
        String consultationDate
) {}
