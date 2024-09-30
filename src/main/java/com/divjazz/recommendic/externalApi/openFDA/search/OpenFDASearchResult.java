package com.divjazz.recommendic.externalApi.openFDA.search;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record OpenFDASearchResult(
        List<OpenFDASearchSubmission> submissions,
        String application_number,
        String sponsor_name,
        OpenFDASearchOpenFDAField openfda
) {
}
