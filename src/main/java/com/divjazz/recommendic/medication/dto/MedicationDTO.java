package com.divjazz.recommendic.medication.dto;


public record MedicationDTO (
        String id,
        String name,
        String dosage,
        String frequency,
        String startDate,
        String endDate,
        String instructions
) {}
