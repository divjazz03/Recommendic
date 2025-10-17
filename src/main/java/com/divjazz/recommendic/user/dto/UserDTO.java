package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;

import java.time.LocalDateTime;

public record UserDTO(
        long id,
        String userId,
        Gender gender,
        LocalDateTime lastLogin,
        UserType userType,
        UserStage userStage,
        UserPrincipal userPrincipal
) {
}

