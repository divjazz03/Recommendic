package com.divjazz.recommendic.user.model.userAttributes.preferences;

import lombok.Builder;

@Builder
public record ConsultantNotificationPreference(
        Boolean emailNotificationEnabled,
        Boolean smsNotificationEnabled,
        Boolean appointmentRemindersEnabled,
        Boolean labResultsUpdateEnabled,
        Boolean systemUpdatesEnabled,
        Boolean marketingEmailEnabled
) {
}
