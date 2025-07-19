package com.divjazz.recommendic.consultation.service;

import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.consultation.exception.ConsultationAlreadyStartedException;
import com.divjazz.recommendic.consultation.exception.ConsultationStartedBeforeAppointmentException;
import com.divjazz.recommendic.consultation.mapper.ConsultationMapper;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.repository.ConsultationCustomRepository;
import com.divjazz.recommendic.consultation.repository.ConsultationProjection;
import com.divjazz.recommendic.consultation.repository.ConsultationRepository;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final AppointmentService appointmentService;
    private final ConsultationRepository consultationRepository;
    private final ConsultationCustomRepository consultationCustomRepository;
    public static final Integer MINUTES_BEFORE_APPOINTED_TIME_FOR_CONSULTATION_TO_START = 15;


    @Transactional
    public ConsultationResponse startConsultation(Long appointmentId) {
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        if (consultationRepository.existsByAppointmentId(appointmentId)) {
            throw new ConsultationAlreadyStartedException("Consultation already started for this appointment");
        }
        // The Consultation should not be started 15 minutes before the actual appointed time
        if (appointment.getStartDateAndTime()
                .minusMinutes(MINUTES_BEFORE_APPOINTED_TIME_FOR_CONSULTATION_TO_START)
                .isAfter(OffsetDateTime.of(LocalDateTime.now(), appointment.getSchedule().getZoneOffset()))) {
            throw new ConsultationStartedBeforeAppointmentException();
        }
        Consultation consultation = Consultation.builder()
                .appointment(appointment)
                .consultationStatus(ConsultationStatus.ONGOING)
                .channel(appointment.getConsultationChannel())
                .startedAt(LocalDateTime.now())
                .build();
        consultation = consultationRepository.save(consultation);
        return ConsultationMapper.consultationToConsultationResponse(consultation);
    }

    @Transactional
    public ConsultationResponse completeConsultation(Long consultationId, String summary) {
        var consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation with id: %s either doesn't exist or has been deleted"));
        consultation.setConsultationStatus(ConsultationStatus.COMPLETED);
        consultation.setEndedAt(LocalDateTime.now());
        consultation.setSummary(summary);
        return ConsultationMapper.consultationToConsultationResponse(consultation);
    }

    @Transactional(readOnly = true)
    public Stream<ConsultationProjection> retrieveConsultationDetailByPatientId(String patientId) {
        return consultationCustomRepository.findConsultationDetailsByPatientUserId(patientId);
    }
    @Transactional(readOnly = true)
    public Stream<ConsultationProjection> retrieveConsultationDetailByConsultantId(String consultantId) {
        return consultationCustomRepository.findConsultationDetailsByConsultantUserId(consultantId);
    }
    public Consultation getConsultationById(Long consultationId) {
        return consultationRepository.findById(consultationId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Consultation with id: %s not found".formatted(consultationId))
                );
    }
}
