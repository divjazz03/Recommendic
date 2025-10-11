package com.divjazz.recommendic.user.controller.consultant.payload;

import com.divjazz.recommendic.user.controller.patient.payload.ConsultantEducationResponse;

public record ConsultantProfileDetails(
        ConsultantProfileFull profile,
        ConsultantEducationResponse education
) {

}
