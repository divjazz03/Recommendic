package com.divjazz.recommendic.appointment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

public enum AppointmentHistory {
    NEW("new"), FOLLOW_UP("follow_up");
    @JsonValue
    @Getter
    private final String value;

    AppointmentHistory(String value) {
        this.value = value;
    }
    @JsonCreator
    public static AppointmentHistory fromValue(String value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("value for appointment history value cannot be null");
        }

        for (AppointmentHistory appointmentHistory : AppointmentHistory.values()) {
            if (appointmentHistory.value.equalsIgnoreCase(value)) {
                return appointmentHistory;
            }

        }
        throw new IllegalArgumentException("%s is not a valid appointment history".formatted(value));
    }
}
