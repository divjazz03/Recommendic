package com.divjazz.recommendic.user.controller.patient.payload;

import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.preferences.PatientNotificationPreference;
import com.divjazz.recommendic.user.model.userAttributes.preferences.UserSecuritySetting;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;
public record PatientProfileUpdateRequest(
        UserName userName,
        String phoneNumber,
        String dateOfBirth,
        Address address,
        Set<String> interests,
        String profileImgUrl,
        UserSecuritySetting securityPreference,
        PatientNotificationPreference notificationPreference
) {
}
