package com.divjazz.recommendic.externalApi.openFDA.count;

import com.divjazz.recommendic.externalApi.openFDA.OpenFDAResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenFDACountResult(String term, long count) implements OpenFDAResult {
}
