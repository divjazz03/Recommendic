package com.divjazz.recommendic.consultation.service;

import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.consultation.exception.ConsultationAlreadyStartedException;
import com.divjazz.recommendic.consultation.exception.ConsultationNotFoundException;
import com.divjazz.recommendic.consultation.mapper.ConsultationMapper;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.repository.ConsultationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final AppointmentService appointmentService;
    private final ConsultationRepository consultationRepository;

    @Transactional
    public ConsultationResponse startConsultation(Long appointmentId) {
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        if (consultationRepository.existsByAppointmentId(appointmentId)) {
            throw new ConsultationAlreadyStartedException("Consultation already started for this appointment");
        }
        Consultation consultation = Consultation.builder()
                .appointment(appointment)
                .consultationStatus(ConsultationStatus.ONGOING)
                .channel(appointment.getScheduleSlot().getConsultationChannel())
                .startedAt(LocalDateTime.now())
                .build();
        consultation = consultationRepository.save(consultation);
        return ConsultationMapper.consultationToConsultationResponse(consultation);
    }

    @Transactional
    public ConsultationResponse completeConsultation(Long consultationId, String summary) {
        var consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ConsultationNotFoundException(consultationId));

        consultation.setConsultationStatus(ConsultationStatus.COMPLETED);
        consultation.setEndedAt(LocalDateTime.now());
        consultation.setSummary(summary);
        return ConsultationMapper.consultationToConsultationResponse(consultation);
    }

    @Transactional(readOnly = true)
    public Stream<Consultation> retrieveConsultationsByPatientId(String patientId) {
        return consultationRepository.findConsultationsByPatientUserId(patientId);
    }
    @Transactional(readOnly = true)
    public Stream<Consultation> retrieveConsultationsByConsultantId(String consultantId) {
        return consultationRepository.findConsultationsByConsultantUserId(consultantId);
    }
}
