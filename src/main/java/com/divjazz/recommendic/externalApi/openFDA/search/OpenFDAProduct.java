package com.divjazz.recommendic.externalApi.openFDA.search;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record OpenFDAProduct(
    String productNumber,
    String reference_drug,
    String brand_name,
    List<OpenFDAActiveIngredient> active_ingredients,
    String reference_standard,
    String dosage_form,
    String route,
    String marketing_status
)
{}
