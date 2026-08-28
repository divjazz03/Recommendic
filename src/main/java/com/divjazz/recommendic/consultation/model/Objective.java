package com.divjazz.recommendic.consultation.model;

import java.util.Set;

public record Objective(
        String bloodPressure,
        String heartRate,
        String temperature,
        String respiratoryRate,
        Set<String> findings
) {
}
