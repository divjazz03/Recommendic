package com.divjazz.recommendic.consultation.dto;

import lombok.Builder;

@Builder
public record ConsultantData(
    String name,
    String title,
    String specialty,
    Integer experience,
    Float rating,
    String location,
    String image
) {
}
