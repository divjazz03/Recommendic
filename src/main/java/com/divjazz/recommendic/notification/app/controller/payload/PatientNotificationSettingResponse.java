package com.divjazz.recommendic.notification.app.controller.payload;

public record PatientNotificationSettingResponse(
         boolean emailNotificationsEnabled,
         boolean smsNotificationsEnabled,
         boolean appointmentRemindersEnabled,
         boolean labResultUpdatesEnabled,
         boolean systemUpdatesEnabled,
         boolean marketingEmailsEnabled
) implements NotificationSettingResponse {
}
