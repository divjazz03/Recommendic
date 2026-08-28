package com.divjazz.recommendic.consultation.dto;

import com.divjazz.recommendic.user.enums.Gender;
import lombok.Builder;

@Builder
public record ConsultantData(
    String name,
    String id,
    String title,
    String specialty,
    Integer experience,
    Float rating,
    String location,
    String image,
    Gender gender
) {
}
