package com.divjazz.recommendic.user.controller;

import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.userAttributes.preferences.UserSecuritySetting;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public record UserResponse(
        long id,
        String userId,
        Gender gender,
        LocalDateTime lastLogin,
        UserType userType,
        UserStage userStage,
        UserPrincipalResponse userPrincipal,
        UserSecuritySetting userSecuritySetting
) {
}
