package com.divjazz.recommendic.external.openFDA;

import com.divjazz.recommendic.external.openFDA.search.OpenFDAMeta;
import com.divjazz.recommendic.external.openFDA.search.OpenFDASearchLabelResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenFDALabelResult(OpenFDAMeta meta,
                                 List<OpenFDASearchLabelResult> results) implements OpenFDAResult {
}
