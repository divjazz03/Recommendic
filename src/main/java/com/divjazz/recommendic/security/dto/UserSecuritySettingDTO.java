package com.divjazz.recommendic.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record UserSecuritySettingDTO(
        Boolean multifactorAuthEnabled,
        Integer sessionTimeoutMin,
        Boolean loginAlertsEnabled
) {
}
