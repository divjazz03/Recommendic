package com.divjazz.recommendic.external.openFDA.count;

import com.divjazz.recommendic.external.openFDA.OpenFDAResult;
import com.divjazz.recommendic.external.openFDA.search.OpenFDAMeta;

import java.util.List;

public record OpenFDACountResultWrapper (
        OpenFDAMeta meta,
        List<OpenFDACountResult> results

) implements OpenFDAResult {
}
