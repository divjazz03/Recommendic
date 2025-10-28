package com.divjazz.recommendic.notification.app.controller.payload;

public record PatientNotificationSettingResponse(
         boolean emailNotificationEnabled,
         boolean smsNotificationEnabled,
         boolean appointmentRemindersEnabled,
         boolean labResultsUpdateEnabled,
         boolean systemUpdatesEnabled,
         boolean marketingEmailEnabled
) implements NotificationSettingResponse {
}
