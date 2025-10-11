package com.divjazz.recommendic.user.controller.patient.payload;

public record ConsultantEducationResponse(
        String year,
        String institution,
        String degree
) {
}
