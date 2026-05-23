package com.divjazz.recommendic.user.model.userAttributes.preferences;

import lombok.Builder;

@Builder
public record UserSecuritySetting(
    Boolean multiFactorAuthEnabled,
    Long sessionTimeoutMin,
    Boolean loginAlertsEnabled
){}
