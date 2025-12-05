package com.divjazz.recommendic.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum BloodType {

    A_PLUS("A+"), A_MINUS("A-"), B_PLUS("B+"), B_MINUS("B-"),
    AB_PLUS("AB+"), AB_MINUS("AB-"), O_PLUS("O+"), O_MINUS("O-");
    @JsonValue
    private final String value;

    BloodType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static BloodType fromValue(String value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Blood type Value cannot be null");
        }
        for (BloodType bloodType: BloodType.values()) {
            if (bloodType.value.equalsIgnoreCase(value)) {
                return bloodType;
            }
        }
        throw new IllegalArgumentException("Value %s is not a valid blood type value".formatted(value));
    }
}
