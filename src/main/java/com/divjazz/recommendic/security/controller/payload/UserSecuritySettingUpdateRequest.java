package com.divjazz.recommendic.security.controller.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserSecuritySettingUpdateRequest(
        Boolean multifactorAuthEnabled,
        Integer sessionTimeoutMin,
        Boolean loginAlertsEnabled
) {
}
