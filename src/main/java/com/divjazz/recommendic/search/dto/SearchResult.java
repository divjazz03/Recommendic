package com.divjazz.recommendic.search.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record SearchResult(List<Map<?,?>> results) {
}
