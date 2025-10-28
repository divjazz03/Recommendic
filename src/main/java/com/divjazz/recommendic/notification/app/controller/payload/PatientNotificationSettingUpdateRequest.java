package com.divjazz.recommendic.notification.app.controller.payload;

public record PatientNotificationSettingUpdateRequest (
        Boolean emailNotificationEnabled,
        Boolean smsNotificationEnabled,
        Boolean appointmentRemindersEnabled,
        Boolean labResultsUpdateEnabled,
        Boolean systemUpdatesEnabled,
        Boolean marketingEmailEnabled
) implements NotificationSettingUpdateRequest {
}
