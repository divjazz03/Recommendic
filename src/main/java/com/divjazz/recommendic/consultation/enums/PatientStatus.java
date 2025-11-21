package com.divjazz.recommendic.consultation.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public enum PatientStatus {

    MONITORING("monitoring"), STABLE("stable"), CRITICAL("critical");
    @JsonValue
    private final String value;
    PatientStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PatientStatus fromValue(String value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("value for patient status enum cannot be null");
        }

        for (PatientStatus patientStatus : PatientStatus.values()) {
            if (patientStatus.value.equalsIgnoreCase(value)) {
                return patientStatus;
            }

        }
        throw new IllegalArgumentException("%s is not a valid patient status".formatted(value));
    }
}
