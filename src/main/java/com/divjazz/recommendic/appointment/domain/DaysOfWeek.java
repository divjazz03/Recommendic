package com.divjazz.recommendic.appointment.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;
@Getter
public enum DaysOfWeek {
    MONDAY("monday"),
    TUESDAY("tuesday"),
    WEDNESDAY("wednesday"),
    THURSDAY("thursday"),
    FRIDAY("friday"),
    SATURDAY("saturday"),
    SUNDAY("sunday");

    @JsonValue
    private final String value;

    DaysOfWeek(String value) {
        this.value = value;
    }
    @JsonCreator
    public static DaysOfWeek fromValue(String value) {
        if (value != null) {
            return Stream.of(DaysOfWeek.values())
                    .filter(v -> v.value.equalsIgnoreCase(value))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Value %s is an invalid Day of the week".formatted(value)));
        }
        throw new IllegalArgumentException("Day of the week value cannot be null");
    }
}
