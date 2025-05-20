package com.divjazz.recommendic.external.openFDA.count;

import com.divjazz.recommendic.external.openFDA.OpenFDAResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenFDACountResult(String term, long count) implements OpenFDAResult {
}
