package com.divjazz.recommendic.externalApi.openFDA;

public enum OpenFDAQueryParameters {

    SPONSOR_NAME("sponsor_name"),
    MARKETING_STATUS("products.marketing_status"),
    BRAND_NAME("openfda.brand_name"),
    ACTIVE_INGREDIENTS("products.active_ingredients"),
    DOSAGE_FORM("products.dosage_form"),
    ROUTE("products.route.exact");


    private final String param;

    OpenFDAQueryParameters(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }
}
