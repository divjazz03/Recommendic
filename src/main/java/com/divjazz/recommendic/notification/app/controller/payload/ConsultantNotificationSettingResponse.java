package com.divjazz.recommendic.notification.app.controller.payload;

public record ConsultantNotificationSettingResponse(
        boolean emailNotificationsEnabled,
        boolean smsNotificationsEnabled,
        boolean appointmentRemindersEnabled,
        boolean labResultUpdatesEnabled,
        boolean systemUpdatesEnabled,
        boolean marketingEmailsEnabled
) implements NotificationSettingResponse{
}
