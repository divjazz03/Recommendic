package com.divjazz.recommendic.external.openFDA.search;

import com.divjazz.recommendic.external.openFDA.OpenFDAResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenFDADrugsSearchResult(
        String application_number,
        String sponsor_name,
        OpenFDASearchOpenFDAField openfda,
        List<OpenFDAProduct> products
) implements OpenFDAResult {
}
