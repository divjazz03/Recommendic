package com.divjazz.recommendic.user.repository.projection;

public record ConsultantEducationProjection (
    String degree,
    String institution,
    int year
){}
