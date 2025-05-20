package com.divjazz.recommendic.external.openFDA;

import com.divjazz.recommendic.external.openFDA.search.OpenFDADrugsSearchResult;
import com.divjazz.recommendic.external.openFDA.search.OpenFDAMeta;
import com.divjazz.recommendic.external.openFDA.search.OpenFDASearchLabelResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenFDADrugResult(OpenFDAMeta meta,
                                List<OpenFDADrugsSearchResult> results) implements OpenFDAResult {
}
