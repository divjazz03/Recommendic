package com.divjazz.recommendic.notification.event.listener;

import com.divjazz.recommendic.appointment.event.*;
import com.divjazz.recommendic.notification.app.dto.NotificationDTO;
import com.divjazz.recommendic.notification.app.enums.NotificationCategory;
import com.divjazz.recommendic.notification.app.service.AppNotificationService;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.enums.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentEventListenerForNotification {
    public static final String APPOINTMENT_REQUESTED_HEADER = "Appointment Request";
    public static final String APPOINTMENT_CONFIRMED_HEADER = "Appointment Confirmed";
    public static final String APPOINTMENT_CANCELLED_HEADER = "Appointment Cancelled";
    public static final String APPOINTMENT_RESCHEDULE_REQUEST_HEADER = "Appointment Reschedule Requested";
    public static final String APPOINTMENT_RESCHEDULE_ACCEPTED_HEADER = "Appointment Reschedule Request Accepted";
    public static final String CONSULTANT_FOLLOWUP_APPOINTMENT_REQUESTED_HEADER = "Follow up requested";
    public static final String CONSULTANT_FOLLOWUP_APPOINTMENT_ACCEPTED_HEADER = "Follow up accepted";
    public static final String APPOINTMENT_REQUESTED_SUMMARY = "You have a new appointment request from %s from %s to %s";
    public static final String APPOINTMENT_REQUESTED_SUMMARY_FOR_REQUESTER = "You made a new appointment request to %s from %s to %s";
    public static final String APPOINTMENT_CONFIRMED_SUMMARY = "Your appointment request to %s from %s to %s has been confirmed";
    public static final String APPOINTMENT_CONFIRMED_SUMMARY_FOR_CONFIRMER = "You have a confirmed appointment with %s from %s to %s";
    public static final String APPOINTMENT_CANCELLED_SUMMARY = "Your appointment with %s from %s to %s was cancelled because %s";
    public static final String APPOINTMENT_CANCELLED_SUMMARY_FOR_CANCELLER = "You have cancelled your appointment with %s from %s to %s was cancelled because %s";
    public static final String APPOINTMENT_RESCHEDULE_REQUESTED_SUMMARY = "%s has requested to reschedule your appointment with them from %s to %s";
    public static final String APPOINTMENT_RESCHEDULE_REQUESTED_SUMMARY_FOR_RESCHEDULER = "You have requested a reschedule of your appointment with %s from %s to %s";
    public static final String APPOINTMENT_RESCHEDULE_ACCEPTED_SUMMARY_FOR_ACCEPTER = "You have accepted %s's request to reschedule your appointment from %s to %s";
    public static final String APPOINTMENT_RESCHEDULE_ACCEPTED_SUMMARY = "%s has accepted your request to reschedule your appointment from %s to %s";
    public static final String CONSULTANT_FOLLOWUP_APPOINTMENT_REQUESTED_SUMMARY_FOR_REQUESTER = "You have requested a follow up from the previous consultation to %s. Time from %s to %s";
    public static final String CONSULTANT_FOLLOWUP_APPOINTMENT_REQUESTED_SUMMARY = "%s has requested a follow up from the previous consultation. Time from %s to %s";
    public static final String CONSULTANT_FOLLOWUP_APPOINTMENT_ACCEPTED_SUMMARY_FOR_ACCEPTER = "You have accepted a follow up request from the previous consultation from %s. Time from %s to %s";
    public static final String CONSULTANT_FOLLOWUP_APPOINTMENT_ACCEPTED_SUMMARY = "%s has accepted your follow up request from the previous consultation. Time from %s to %s";

    private final AppNotificationService appNotificationService;
    private final AuthUtils authUtils;


    @EventListener
    public void onAppointmentEvent(AppointmentEvent appointmentEvent) {
        var userDetails = authUtils.getCurrentUser();
        switch (appointmentEvent.appointmentEventType()) {
            case APPOINTMENT_CANCELLED -> {
                var eventData = (AppointmentCancelledData) appointmentEvent;
                var cancellerName = userDetails.userType() == UserType.PATIENT? eventData.patientName() : eventData.consultantName();
                var cancellerId = userDetails.userType() == UserType.PATIENT? eventData.patientId() : eventData.consultantId();
                var otherPersonName = userDetails.userType() == UserType.PATIENT? eventData.consultantName(): eventData.patientName();
                var otherPersonId = userDetails.userType() == UserType.PATIENT? eventData.consultantId(): eventData.patientId();

                    appNotificationService.createNotification(new NotificationDTO(
                            APPOINTMENT_CANCELLED_HEADER,
                            APPOINTMENT_CANCELLED_SUMMARY_FOR_CANCELLER.formatted(otherPersonName, eventData.startDateTime(), eventData.endDateTime(), eventData.reason()),
                            cancellerId,
                            eventData.appointmentId(),
                            false,
                            NotificationCategory.APPOINTMENT
                    ));
                    appNotificationService.createNotification(new NotificationDTO(
                            APPOINTMENT_CANCELLED_HEADER,
                            APPOINTMENT_CANCELLED_SUMMARY.formatted(cancellerName,eventData.startDateTime(),eventData.endDateTime(), eventData.reason()),
                            otherPersonId,
                            eventData.appointmentId(),
                            false,
                            NotificationCategory.APPOINTMENT
                    ));
            }
            case APPOINTMENT_REQUESTED -> {
                var eventData = (AppointmentRequestedData) appointmentEvent;
                var requesterName = eventData.patientName();
                var requesterId = eventData.patientId();
                appNotificationService.createNotification(new NotificationDTO(
                        APPOINTMENT_REQUESTED_HEADER,
                        APPOINTMENT_REQUESTED_SUMMARY.formatted(requesterName,eventData.startDateTime(), eventData.endDateTime()),
                        eventData.consultantId(),
                        eventData.appointmentId(),
                        false,
                        NotificationCategory.APPOINTMENT
                ));
                appNotificationService.createNotification(new NotificationDTO(
                        APPOINTMENT_REQUESTED_HEADER,
                        APPOINTMENT_REQUESTED_SUMMARY_FOR_REQUESTER.formatted(eventData.consultantName(), eventData.startDateTime(),eventData.endDateTime()),
                        requesterId,
                        eventData.appointmentId(),
                        false,
                        NotificationCategory.APPOINTMENT
                ));
            }
            case APPOINTMENT_CONFIRMED -> {
                var eventData = (AppointmentConfirmedData) appointmentEvent;

                var confirmerName = userDetails.userType() == UserType.PATIENT? eventData.patientName() : eventData.consultantName();
                var confirmerId = userDetails.userType() == UserType.PATIENT? eventData.patientId() : eventData.consultantId();
                var otherPersonName = userDetails.userType() == UserType.PATIENT? eventData.consultantName(): eventData.patientName();
                var otherPersonId = userDetails.userType() == UserType.PATIENT? eventData.consultantId(): eventData.patientId();
                appNotificationService.createNotification(new NotificationDTO(
                        APPOINTMENT_CONFIRMED_HEADER,
                        APPOINTMENT_CONFIRMED_SUMMARY_FOR_CONFIRMER.formatted(confirmerName,eventData.startDateTime(), eventData.endDateTime()),
                        confirmerId,
                        eventData.appointmentId(),
                        false,
                        NotificationCategory.APPOINTMENT
                ));

                appNotificationService.createNotification(new NotificationDTO(
                        APPOINTMENT_CONFIRMED_HEADER,
                        APPOINTMENT_CONFIRMED_SUMMARY.formatted(otherPersonName,eventData.startDateTime(), eventData.endDateTime()),
                        otherPersonId,
                        eventData.appointmentId(),
                        false,
                        NotificationCategory.APPOINTMENT
                ));
            }
            case APPOINTMENT_RESCHEDULE_REQUESTED -> {
                var eventData = (AppointmentRescheduleRequestedData) appointmentEvent;
                var rescheduleRequesterName = userDetails.userType() == UserType.PATIENT? eventData.patientName() : eventData.consultantName();
                var rescheduleRequesterId = userDetails.userType() == UserType.PATIENT? eventData.patientId() : eventData.consultantId();
                var otherPersonName = userDetails.userType() == UserType.PATIENT? eventData.consultantName(): eventData.patientName();
                var otherPersonId = userDetails.userType() == UserType.PATIENT? eventData.consultantId(): eventData.patientId();
                appNotificationService.createNotification(new NotificationDTO(
                        APPOINTMENT_RESCHEDULE_REQUEST_HEADER,
                        APPOINTMENT_RESCHEDULE_REQUESTED_SUMMARY_FOR_RESCHEDULER.formatted(otherPersonName,eventData.previousDateTime(), eventData.proposedDateTime()),
                        rescheduleRequesterId,
                        eventData.appointmentId(),
                        false,
                        NotificationCategory.APPOINTMENT
                ));

                appNotificationService.createNotification(new NotificationDTO(
                        APPOINTMENT_RESCHEDULE_REQUEST_HEADER,
                        APPOINTMENT_RESCHEDULE_REQUESTED_SUMMARY.formatted(rescheduleRequesterName,eventData.previousDateTime(), eventData.proposedDateTime()),
                        otherPersonId,
                        eventData.appointmentId(),
                        false,
                        NotificationCategory.APPOINTMENT
                ));
            }
            case APPOINTMENT_RESCHEDULE_ACCEPTED -> {
                var eventData = (AppointmentRescheduleAcceptedData) appointmentEvent;
                var rescheduleAccepterName = userDetails.userType() == UserType.PATIENT? eventData.patientFullName() : eventData.consultantFullName();
                var rescheduleAccepterId = userDetails.userType() == UserType.PATIENT? eventData.patientId() : eventData.consultantId();
                var otherPersonName = userDetails.userType() == UserType.PATIENT? eventData.consultantFullName(): eventData.patientFullName();
                var otherPersonId = userDetails.userType() == UserType.PATIENT? eventData.consultantId(): eventData.patientId();
                appNotificationService.createNotification(new NotificationDTO(
                        APPOINTMENT_RESCHEDULE_ACCEPTED_HEADER,
                        APPOINTMENT_RESCHEDULE_ACCEPTED_SUMMARY_FOR_ACCEPTER.formatted(otherPersonName,eventData.previousDateTime(), eventData.proposedDateTime()),
                        rescheduleAccepterId,
                        eventData.appointmentId(),
                        false,
                        NotificationCategory.APPOINTMENT
                ));

                appNotificationService.createNotification(new NotificationDTO(
                        APPOINTMENT_RESCHEDULE_ACCEPTED_HEADER,
                        APPOINTMENT_RESCHEDULE_ACCEPTED_SUMMARY.formatted(rescheduleAccepterName,eventData.previousDateTime(), eventData.proposedDateTime()),
                        otherPersonId,
                        eventData.appointmentId(),
                        false,
                        NotificationCategory.APPOINTMENT
                ));

            }
            case CONSULTANT_FOLLOWUP_APPOINTMENT_REQUESTED -> {
                var eventData = (ConsultantFollowUpAppointmentRequestedData) appointmentEvent;
                var followUpRequesterName = eventData.consultantFullName();
                var followUpRequesterId = eventData.consultantId();
                var otherPersonId = eventData.patientId();
                var otherPersonName = eventData.patientFullName();
                appNotificationService.createNotification(new NotificationDTO(
                        CONSULTANT_FOLLOWUP_APPOINTMENT_REQUESTED_HEADER,
                        CONSULTANT_FOLLOWUP_APPOINTMENT_REQUESTED_SUMMARY_FOR_REQUESTER.formatted(otherPersonName, eventData.startDateTime(), eventData.endDateTime()),
                        followUpRequesterId,
                        eventData.appointmentId(),
                        false,
                        NotificationCategory.APPOINTMENT
                ));

                appNotificationService.createNotification(new NotificationDTO(
                        CONSULTANT_FOLLOWUP_APPOINTMENT_REQUESTED_HEADER,
                        CONSULTANT_FOLLOWUP_APPOINTMENT_REQUESTED_SUMMARY.formatted(followUpRequesterName,eventData.startDateTime(), eventData.endDateTime()),
                        otherPersonId,
                        eventData.appointmentId(),
                        false,
                        NotificationCategory.APPOINTMENT
                ));
            }
            case CONSULTANT_FOLLOWUP_APPOINTMENT_CONFIRMED -> {
                var eventData = (ConsultantFollowUpAppointmentAcceptedData) appointmentEvent;
                var followUpAccepterName = eventData.patientFullName();
                var followUpAccepterId = eventData.patientId();
                var otherPersonId = eventData.consultantId();
                var otherPersonName = eventData.consultantFullName();
                appNotificationService.createNotification(new NotificationDTO(
                        CONSULTANT_FOLLOWUP_APPOINTMENT_ACCEPTED_HEADER,
                        CONSULTANT_FOLLOWUP_APPOINTMENT_ACCEPTED_SUMMARY_FOR_ACCEPTER.formatted(otherPersonName, eventData.startDateTime(), eventData.endDateTime()),
                        followUpAccepterId,
                        eventData.appointmentId(),
                        false,
                        NotificationCategory.APPOINTMENT
                ));

                appNotificationService.createNotification(new NotificationDTO(
                        CONSULTANT_FOLLOWUP_APPOINTMENT_ACCEPTED_HEADER,
                        CONSULTANT_FOLLOWUP_APPOINTMENT_ACCEPTED_SUMMARY.formatted(followUpAccepterName,eventData.startDateTime(), eventData.endDateTime()),
                        otherPersonId,
                        eventData.appointmentId(),
                        false,
                        NotificationCategory.APPOINTMENT
                ));
            }

        }
    }
}
