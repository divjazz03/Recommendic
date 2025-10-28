package com.divjazz.recommendic.notification.app.controller.payload;

public record ConsultantNotificationSettingUpdateRequest(
        Boolean emailNotificationEnabled,
        Boolean smsNotificationEnabled,
        Boolean appointmentRemindersEnabled,
        Boolean labResultsUpdateEnabled,
        Boolean systemUpdatesEnabled,
        Boolean marketingEmailEnabled
) implements NotificationSettingUpdateRequest {
}
