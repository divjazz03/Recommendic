package com.divjazz.recommendic.appointment.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum RecurrenceFrequency {
    ONE_OFF("one_off"), DAILY("daily"), WEEKLY("weekly"), MONTHLY("monthly");

    @JsonValue
    private final String value;

    RecurrenceFrequency(String value) {
        this.value = value;
    }

    public static RecurrenceFrequency fromValue(String value) {
        if (value != null) {
            Stream.of(RecurrenceFrequency.values())
                    .filter(v -> v.value.equalsIgnoreCase(value))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Value %s is an invalid recurrence frequency"));
        }
        throw new IllegalArgumentException("Value cannot be null");
    }
}
