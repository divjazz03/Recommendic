package com.divjazz.recommendic.externalApi.openFDA.search;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record OpenFDASearchOpenFDAField(
        List<String> application_number,
        List<String> brand_name,
        List<String> generic_name,
        List<String> manufacturer_name,
        List<String> product_ndc,
        List<String> product_type,
        List<String> route,
        List<String> substance_name,
        List<String> rxcui,
        List<String> spl_id,
        List<String> spl_set_id,
        List<String> package_ndc,
        List<String> nui,
        List<String> pharm_class_mao,
        List<String> pharm_class_cs,
        List<String> pharm_class_epc,
        List<String> unii
) {
}