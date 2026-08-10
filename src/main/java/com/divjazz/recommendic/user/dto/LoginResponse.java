package com.divjazz.recommendic.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record LoginResponse(
        String userId,
        String role,
        String refreshToken,
        String accessToken,
        String userStage

) {
}
