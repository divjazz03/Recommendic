package com.divjazz.recommendic.externalApi.openFDA.search;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record OpenFDASearchSubmissionApplicationDocs(
        String id,
        String url,
        String date,
        String type
) {
}
