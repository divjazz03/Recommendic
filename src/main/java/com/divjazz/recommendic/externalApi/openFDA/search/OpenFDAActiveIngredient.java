package com.divjazz.recommendic.externalApi.openFDA.search;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record OpenFDAActiveIngredient(String name, String strength) {
}
