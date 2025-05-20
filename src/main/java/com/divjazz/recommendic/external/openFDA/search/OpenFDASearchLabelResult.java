package com.divjazz.recommendic.external.openFDA.search;

import com.divjazz.recommendic.external.openFDA.OpenFDAResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record OpenFDASearchLabelResult(
        List<String> abuse,
        List<String> active_ingredient,
        List<String> inactive_ingredient,
        List<String> accessories,
        List<String> adverse_reaction,
        List<String> alarms,
        List<String> dosage_and_administration,
        List<String> other_safety_information,
        List<String> boxed_warning,
        List<String> description,
        List<String> purpose,
        List<String> keep_out_of_reach_of_children,
        List<String> warnings,
        List<String> warnings_and_cautions,
        List<String> pregnancy_or_breast_feeding,
        List<String> stop_use,
        List<String> do_not_use,
        List<String> risks,
        List<String> precautions,
        List<String> storage_and_handling,
        OpenFDASearchOpenFDAField openfda
) implements OpenFDAResult {
}
