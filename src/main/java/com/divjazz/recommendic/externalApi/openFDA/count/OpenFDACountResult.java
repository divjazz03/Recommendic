package com.divjazz.recommendic.externalApi.openFDA.count;

import com.divjazz.recommendic.externalApi.openFDA.OpenFDAResult;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record OpenFDACountResult(String term, long count) implements OpenFDAResult {

}
