package com.divjazz.recommendic.consultation.model;

import java.util.Set;

public record Assessment(
        String clinicalImpression,
        String reasoning,
        Set<Diagnosis> diagnosis
) {
}
