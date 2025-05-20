package com.divjazz.recommendic.external.openFDA.search;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record OpenFDAMetaResult(int skip, int limit, int total) {
}
