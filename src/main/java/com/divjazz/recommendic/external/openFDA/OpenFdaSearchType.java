package com.divjazz.recommendic.external.openFDA;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OpenFdaSearchType {

    LABEL("label"),DRUG("drug"),ADVERSE_EFFECT("adverse_effect");

    @JsonValue
    private final String value;
    OpenFdaSearchType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OpenFdaSearchType fromValue(String value) {

        for (OpenFdaSearchType type : OpenFdaSearchType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: %s".formatted(value));
    }
}
