package com.divjazz.recommendic.externalApi.openFDA.search;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record OpenFDAMeta(String disclaimer, String terms,String license, String last_updated, OpenFDAMetaResult results) {
}
