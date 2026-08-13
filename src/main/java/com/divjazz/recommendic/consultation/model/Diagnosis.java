package com.divjazz.recommendic.consultation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record Diagnosis(
        @Column(name = "consultation_id", insertable = false, updatable = false)
        String consultationId,
        String code,
        String codingSystem,
        String name,
        @Enumerated(EnumType.STRING) DiagnosisRole type,
        @Enumerated(EnumType.STRING) DiagnosisStatus status
) {
    enum DiagnosisRole {
        PRIMARY,SECONDARY, DIFFERENTIAL
    }
    enum DiagnosisStatus{
        CONSIDERED,SUSPECTED, RULED_OUT, CONFIRMED, RESOLVED
    }
}
