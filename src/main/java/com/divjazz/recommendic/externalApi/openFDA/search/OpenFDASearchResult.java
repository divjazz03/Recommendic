package com.divjazz.recommendic.externalApi.openFDA.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenFDASearchResult(
        String application_number,
        String sponsor_name,
        OpenFDASearchOpenFDAField openfda,
        List<OpenFDAProduct> products
) {
}
