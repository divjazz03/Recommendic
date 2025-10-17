package com.divjazz.recommendic.user.controller.consultant.payload;

import com.divjazz.recommendic.user.dto.ConsultantEducationDTO;

public record ConsultantProfileUpdateRequest(
        ConsultantEducationDTO education,
        ConsultantProfileFull profile
) {
}
