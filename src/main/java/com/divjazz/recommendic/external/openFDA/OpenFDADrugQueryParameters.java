package com.divjazz.recommendic.external.openFDA;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OpenFDADrugQueryParameters {

    MISSING("_missing_"),
    EXISTS("_exists_"),
    SPONSOR_NAME("sponsor_name"),
    BRAND_NAME("openfda.brand_name"),
    GENERIC_NAME("openfda.generic_name"),
    MANUFACTURER_NAME("openfda.manufacturer_name"),
    ACTIVE_INGREDIENTS("products.active_ingredients"),
    DOSAGE_FORM("products.dosage_form"),
    MARKETING_STATUS("products.marketing_status"),
    PRODUCT_BRAND_NAME("products.brand_name"),
    ROUTE("products.route"),
    PRODUCT_ACTIVE_INGREDIENT_NAME("products.active_ingredients.name"),
    PRODUCT_ACTIVE_INGREDIENT_STRENGTH("products.active_ingredients.strength");

    @JsonValue
    private final String value;

    OpenFDADrugQueryParameters(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OpenFDADrugQueryParameters fromValue(String value) {
        for (OpenFDADrugQueryParameters params : OpenFDADrugQueryParameters.values()) {
            if (params.getValue().equalsIgnoreCase(value)){
                return params;
            }
        }
        throw new IllegalArgumentException("Invalid name: %s".formatted(value));
    }
}
