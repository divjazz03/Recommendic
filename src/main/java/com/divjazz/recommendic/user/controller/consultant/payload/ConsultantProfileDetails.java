package com.divjazz.recommendic.user.controller.consultant.payload;

import com.divjazz.recommendic.user.controller.patient.payload.ConsultantEducationResponse;

import java.util.Set;

public record ConsultantProfileDetails(
        ConsultantProfileFull profile,
        Set<ConsultantEducationResponse> educations
) {

}
