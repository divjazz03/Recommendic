package com.divjazz.recommendic.user.controller.consultant.payload;

public record ConsultantProfileUpdateRequest(
        ConsultantEducation education,
        ConsultantProfileFull profile
) {
}
