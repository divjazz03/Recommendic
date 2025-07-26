package com.divjazz.recommendic.appointment.event;

import com.divjazz.recommendic.notification.dto.NotificationDTO;
import com.divjazz.recommendic.notification.enums.NotificationCategory;
import com.divjazz.recommendic.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentEventListener {
    public static final String APPOINTMENT_REQUESTED_HEADER = "Appointment request";
    public static final String APPOINTMENT_CONFIRMED_HEADER = "Appointment confirmed";
    public static final String APPOINTMENT_REQUESTED_SUMMARY = "You have a new appointment request from %s at %s to %s";
    public static final String APPOINTMENT_CONFIRMED_SUMMARY = "You have a confirmed appointment with %s from %s to %s";

    private final NotificationService notificationService;

    @EventListener
    public void onAppointmentEvent(AppointmentEvent appointmentEvent) {
        switch (appointmentEvent.getAppointmentEventType()) {
            case APPOINTMENT_CANCELLED -> {
            }
            case APPOINTMENT_REQUESTED -> {
                var userFullName = (String) appointmentEvent.getData().get("name");
                var targetId = (String) appointmentEvent.getData().get("targetId");
                var startDateTime = (String) appointmentEvent.getData().get("startDateTime");
                var endDateTime = (String) appointmentEvent.getData().get("endDateTime");
                notificationService.createNotification(new NotificationDTO(
                        APPOINTMENT_REQUESTED_HEADER,
                        APPOINTMENT_REQUESTED_SUMMARY.formatted(userFullName,startDateTime, endDateTime),
                        targetId,
                        false,
                        NotificationCategory.NEW_APPOINTMENT
                ));
            }
            case APPOINTMENT_CONFIRMED -> {
                var consultantFullName = (String) appointmentEvent.getData().get("name");
                var targetId = (String) appointmentEvent.getData().get("targetId");
                var startDateTime = (String) appointmentEvent.getData().get("startDateTime");
                var endDateTime = (String) appointmentEvent.getData().get("endDateTime");
                notificationService.createNotification(new NotificationDTO(
                        APPOINTMENT_CONFIRMED_HEADER,
                        APPOINTMENT_CONFIRMED_SUMMARY.formatted(consultantFullName,startDateTime, endDateTime),
                        targetId,
                        false,
                        NotificationCategory.NEW_APPOINTMENT
                ));
            }
        }
    }
}
