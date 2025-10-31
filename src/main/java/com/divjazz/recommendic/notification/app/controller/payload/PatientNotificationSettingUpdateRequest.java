package com.divjazz.recommendic.notification.app.controller.payload;

public record PatientNotificationSettingUpdateRequest (
        Boolean emailNotificationsEnabled,
        Boolean smsNotificationsEnabled,
        Boolean appointmentRemindersEnabled,
        Boolean labResultUpdatesEnabled,
        Boolean systemUpdatesEnabled,
        Boolean marketingEmailsEnabled
) implements NotificationSettingUpdateRequest {
}
