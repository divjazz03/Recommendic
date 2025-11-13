package com.divjazz.recommendic.appointment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public enum AppointmentStatus {
    PENDING("pending"), CONFIRMED("confirmed"), CANCELLED("cancelled"), RESCHEDULED("rescheduled");

    @JsonValue
    private final String value;

    AppointmentStatus(String value) {
        this.value = value;
    }
    @JsonCreator
    private static AppointmentStatus fromValue(String value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("value for Appointment status enum cannot be null");
        }

        for (AppointmentStatus status : AppointmentStatus.values()) {
            if (value.equalsIgnoreCase(status.value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("value %s is not a valid appointment status".formatted(value));
    }
}
