package com.divjazz.recommendic.consultation.event.listeners;

import com.divjazz.recommendic.appointment.event.AppointmentConfirmedData;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.repository.ConsultationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentListenerForConsultation {
    private final AppointmentService appointmentService;
    private final ConsultationRepository consultationRepository;
    @EventListener
    public void onAppointmentConfirmedEvent (AppointmentConfirmedData appointmentConfirmedData) {
        var appointment = appointmentService.getAppointmentByAppointmentId(appointmentConfirmedData.appointmentId());
        Consultation consultation = new Consultation(appointment,appointment.getConsultationChannel());
        consultationRepository.save(consultation);
    }
}
