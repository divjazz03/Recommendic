package com.divjazz.recommendic.external.openFDA.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenFDASearchOpenFDAField(
        List<String> application_number,
        List<String> brand_name,
        List<String> generic_name,
        List<String> manufacturer_name,
        List<String> product_ndc,
        List<String> product_type,
        List<String> route,
        List<String> substance_name
){
}
