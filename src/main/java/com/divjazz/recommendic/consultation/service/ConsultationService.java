package com.divjazz.recommendic.consultation.service;

import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.consultation.dto.ConsultationCompleteRequest;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.consultation.enums.PatientStatus;
import com.divjazz.recommendic.consultation.event.ConsultationEndedWithRescheduleData;
import com.divjazz.recommendic.consultation.event.ConsultationEndedWithoutFollowUpData;
import com.divjazz.recommendic.consultation.exception.ConsultationAlreadyStartedException;
import com.divjazz.recommendic.consultation.exception.ConsultationStartedBeforeAppointmentException;
import com.divjazz.recommendic.consultation.mapper.ConsultationMapper;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.model.ConsultationSession;
import com.divjazz.recommendic.consultation.repository.ConsultationCustomRepository;
import com.divjazz.recommendic.consultation.repository.ConsultationProjection;
import com.divjazz.recommendic.consultation.repository.ConsultationRepository;
import com.divjazz.recommendic.consultation.repository.ConsultationSessionRepository;
import com.divjazz.recommendic.global.exception.AuthorizationException;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.ReviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final AppointmentService appointmentService;
    private final ConsultationRepository consultationRepository;
    private final ConsultationCustomRepository consultationCustomRepository;
    private final AuthUtils authUtils;
    public static final Integer MINUTES_BEFORE_APPOINTED_TIME_FOR_CONSULTATION_TO_START = 15;
    private final ConsultationSessionRepository consultationSessionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Transactional
    public ConsultationResponse startConsultation(String appointmentId) {
        Appointment appointment = appointmentService.getAppointmentByAppointmentId(appointmentId);
        var currentUser = authUtils.getCurrentUser();
        if (!(currentUser.userId().equals(appointment.getPatient().getUserId()) ||
                currentUser.userId().equals(appointment.getConsultant().getUserId()))) {
            throw new AuthorizationException("You are not registered for this appointment");
        }
        if (consultationRepository.existsByAppointment_AppointmentId(appointmentId)) {
            throw new ConsultationAlreadyStartedException("Consultation already started for this appointment");
        }

        // The Consultation should not be started 15 minutes before the actual appointed time
        if (appointment.getStartDateAndTime()
                .minusMinutes(MINUTES_BEFORE_APPOINTED_TIME_FOR_CONSULTATION_TO_START)
                .isAfter(LocalDateTime.now())) {
            throw new ConsultationStartedBeforeAppointmentException();
        }
        Consultation consultation = new Consultation(appointment,appointment.getConsultationChannel());
        consultation = consultationRepository.save(consultation);
        ConsultationSession consultationSession = new ConsultationSession(appointment.getPatient(), appointment.getConsultant(), consultation);
        var session = consultationSessionRepository.save(consultationSession);
        return ConsultationMapper.consultationToConsultationResponse(consultation);
    }

    @Transactional
    public ConsultationResponse completeConsultation(ConsultationCompleteRequest completeRequest) {
        var consultation = consultationRepository.findByConsultationId(completeRequest.consultationId())
                .orElseThrow(() -> new EntityNotFoundException("Consultation with id: %s either doesn't exist or has been deleted"));
        consultation.setConsultationStatus(ConsultationStatus.COMPLETED);
        consultation.setEndedAt(LocalDateTime.now());
        consultation.setSummary(completeRequest.summary());

        var session = consultation.getSession();
        session.setPatientStatus(PatientStatus.fromValue(completeRequest.patientStatus()));
        session.setCondition(completeRequest.summary());

        if (completeRequest.shouldReschedule()) {
            var event = new ConsultationEndedWithRescheduleData(
                    consultation.getConsultationId(),
                    completeRequest.scheduleId(),
                    completeRequest.date(),
                    consultation.getChannel(),
                    consultation.getAppointment().getConsultant().getUserId(),
                    completeRequest.reason()
            );
            applicationEventPublisher.publishEvent(event);
        }else {
            var event = new ConsultationEndedWithoutFollowUpData(
                    consultation.getConsultationId(),
                    consultation.getAppointment().getPatient().getUserId(),
                    consultation.getAppointment().getConsultant().getUserId()
            );
            applicationEventPublisher.publishEvent(event);
        }
        return ConsultationMapper.consultationToConsultationResponse(consultation);
    }

    @Transactional(readOnly = true)
    public Stream<ConsultationProjection> retrieveConsultationDetailByPatientId(String patientId) {
        return consultationRepository.findConsultationByPatientId(patientId).stream();
    }
    @Transactional(readOnly = true)
    public Stream<ConsultationProjection> retrieveConsultationDetailByConsultantId(String consultantId) {
        return consultationRepository.findConsultationByConsultantId(consultantId).stream();
    }
    public Consultation getConsultationById(String consultationId) {
        return consultationRepository.findByConsultationId(consultationId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Consultation with id: %s not found".formatted(consultationId))
                );
    }
    public Set<ReviewDTO> retrieveReviewsByConsultantId(String consultantId) {
        return consultationRepository.findReviewsForConsultant(consultantId);
    }
}
