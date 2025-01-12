package com.divjazz.recommendic.externalApi.openFDA.search;

import com.divjazz.recommendic.externalApi.openFDA.OpenFDAResult;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record OpenFDAApplicationResult(OpenFDAMeta meta, List<OpenFDASearchResult> results) implements OpenFDAResult {
}
