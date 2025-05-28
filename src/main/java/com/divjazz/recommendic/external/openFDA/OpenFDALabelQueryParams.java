package com.divjazz.recommendic.external.openFDA;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OpenFDALabelQueryParams {

    MISSING("_missing_"),
    EXISTS("_exists_"),

    BRAND_NAME("openfda.brand_name"),
    GENERIC_NAME("openfda.generic_name"),
    MANUFACTURER_NAME("openfda.manufacturer_name"),
    PRODUCT_TYPE("openfda.product_type"),
    ROUTE("openfda.route"),

    ;

    @JsonValue
    private final String value;

    OpenFDALabelQueryParams(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OpenFDALabelQueryParams fromValue(String value) {
        for (OpenFDALabelQueryParams params : OpenFDALabelQueryParams.values()) {
            if (params.getValue().equalsIgnoreCase(value)){
                return params;
            }
        }
        throw new IllegalArgumentException("Invalid name: %s".formatted(value));
    }
}
