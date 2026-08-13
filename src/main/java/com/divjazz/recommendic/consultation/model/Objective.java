package com.divjazz.recommendic.consultation.model;

public record Objective(
        String bloodPressure,
        Integer heartRate,
        Integer temperature,
        Integer respiratoryRate,
        String generalExamination
) {
}
