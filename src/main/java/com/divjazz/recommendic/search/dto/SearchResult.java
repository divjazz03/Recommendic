package com.divjazz.recommendic.search.dto;

import com.divjazz.recommendic.search.enums.Category;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record SearchResult(Category category, Set<?> results) {
}
