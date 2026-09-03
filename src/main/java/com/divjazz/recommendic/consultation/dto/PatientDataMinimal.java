package com.divjazz.recommendic.consultation.dto;

import com.divjazz.recommendic.user.enums.BloodType;
import com.divjazz.recommendic.user.enums.Gender;
import lombok.Builder;

@Builder
public record PatientDataMinimal(
        String name,
        String age,
        Gender gender,
        String id,
        String profileUrl,
        BloodType bloodType
) {
}
