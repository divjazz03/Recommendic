package com.divjazz.recommendic.user.controller.consultant.payload;

import com.divjazz.recommendic.user.controller.patient.payload.ConsultantEducationResponse;
import com.divjazz.recommendic.user.dto.ConsultantEducationDTO;
import com.divjazz.recommendic.user.model.userAttributes.preferences.ConsultantNotificationPreference;
import com.divjazz.recommendic.user.model.userAttributes.preferences.UserSecuritySetting;
import lombok.Builder;

import java.util.Set;

@Builder
public record ConsultantProfileDetails(
        ConsultantProfileFull profile,
        Set<ConsultantEducationDTO> educations,
        ConsultantNotificationPreference notificationPreference,
        UserSecuritySetting securityPreference
) {

}
