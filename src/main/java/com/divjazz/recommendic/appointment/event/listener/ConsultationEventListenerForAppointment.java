package com.divjazz.recommendic.appointment.event.listener;

import com.divjazz.recommendic.appointment.controller.payload.AppointmentCreationRequest;
import com.divjazz.recommendic.appointment.enums.AppointmentHistory;
import com.divjazz.recommendic.appointment.event.ConsultantFollowUpAppointmentRequestedData;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.consultation.event.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Component
public class ConsultationEventListenerForAppointment {

    private final AppointmentService appointmentService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ConsultationEventListenerForAppointment(AppointmentService appointmentService, ApplicationEventPublisher applicationEventPublisher) {
        this.appointmentService = appointmentService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @TransactionalEventListener
    public void onConsultationEvent(ConsultationEventData eventData) {
        if (Objects.isNull(eventData)) return;
        if (eventData.getEvent()
                == ConsultationEvent.CONSULTATION_ENDED_WITH_FOLLOWUP) {
            var data = (ConsultationEndedWithRescheduleData) eventData;

            var appointment = appointmentService.createAppointment(
                    new AppointmentCreationRequest(
                            data.consultantId(),
                            data.scheduleId(),
                            data.channel().name(),
                            data.rescheduleDate(),
                            data.reason(),
                            AppointmentHistory.FOLLOW_UP
                    )
            );
            var appointmentEvent = new ConsultantFollowUpAppointmentRequestedData(
                    appointment.appointmentId(),
                    appointment.consultantId(),
                    appointment.channel(),
                    appointment.patientId(),
                    appointment.consultantFullName(),
                    appointment.patientFullName(),
                    appointment.startTime(),
                    appointment.endTime(),
                    appointment.status()
            );
            applicationEventPublisher.publishEvent(appointmentEvent);
        }
    }
}
