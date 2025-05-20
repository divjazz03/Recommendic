package com.divjazz.recommendic.external.openFDA.search;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record OpenFDAMeta(String disclaimer, String terms, String license, String last_updated,
                          OpenFDAMetaResult results) {
}
