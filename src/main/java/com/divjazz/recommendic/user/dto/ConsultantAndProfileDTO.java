package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;

public record ConsultantAndProfileDTO(
    Consultant consultant,
    ConsultantProfile consultantProfile
) {
}
