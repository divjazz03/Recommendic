package com.divjazz.recommendic.user.controller.consultant.payload;

import com.divjazz.recommendic.user.dto.ConsultantEducationDTO;
import com.divjazz.recommendic.user.model.userAttributes.preferences.ConsultantNotificationPreference;
import com.divjazz.recommendic.user.model.userAttributes.preferences.UserSecuritySetting;

public record ConsultantProfileUpdateRequest(
        ConsultantEducationDTO education,
        ConsultantProfileFull profile,
        ConsultantNotificationPreference notificationPreference,
        UserSecuritySetting securityPreference

) {
}
