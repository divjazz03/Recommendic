package com.divjazz.recommendic.consultation.service;

import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.consultation.controller.payload.CheckInResponse;
import com.divjazz.recommendic.consultation.controller.payload.JoinResponse;
import com.divjazz.recommendic.consultation.controller.payload.StartResponse;
import com.divjazz.recommendic.consultation.dto.ConsultationCompleteRequest;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.consultation.event.ConsultationEndedWithoutFollowUpData;
import com.divjazz.recommendic.consultation.exception.ConsultationStartedBeforeAppointmentException;
import com.divjazz.recommendic.consultation.mapper.ConsultationMapper;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.model.ConsultationSession;
import com.divjazz.recommendic.consultation.repository.ConsultationCustomRepository;
import com.divjazz.recommendic.consultation.repository.ConsultationRepository;
import com.divjazz.recommendic.consultation.repository.ConsultationSessionRepository;
import com.divjazz.recommendic.consultation.repository.projection.ConsultationProjection;
import com.divjazz.recommendic.consultation.sse.ConsultationSSEService;
import com.divjazz.recommendic.global.exception.AppBadRequestException;
import com.divjazz.recommendic.global.exception.AuthorizationException;
import com.divjazz.recommendic.global.exception.ConflictException;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.PatientMedicalData;
import com.divjazz.recommendic.user.dto.ReviewDTO;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.video.StreamVideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationService {

    public static final Integer MINUTES_BEFORE_APPOINTED_TIME_FOR_CONSULTATION_TO_START = 15;
    private final AppointmentService appointmentService;
    private final ConsultationRepository consultationRepository;
    private final ConsultationCustomRepository consultationCustomRepository;
    private final AuthUtils authUtils;
    private final ConsultationSessionRepository consultationSessionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ConsultationMapper consultationMapper;
    private final Environment env;
    private final StreamVideoService streamVideoService;
    private final ConsultationSSEService consultationSSEService;


    @Transactional
    public StartResponse startConsultation(String consultationId) {
        LocalDateTime dateTimeOfRequest = LocalDateTime.now();
        var currentUser = authUtils.getCurrentUser();
        Consultation consultation = consultationRepository.findConsultationByConsultationId(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find consultation with id"));
        Appointment appointment = consultation.getAppointment();
        if (!(currentUser.userId().equals(appointment.getPatient().getUserId()) ||
                currentUser.userId().equals(appointment.getConsultant().getUserId()))) {
            throw new AuthorizationException("You are not registered for this appointment");
        }
        if (!currentUser.userType().equals(UserType.CONSULTANT)) {
            throw new AuthorizationException("You cannot start this consultation. Only the consultant can");
        }
        var statusesIndicatingEndOfConsultation = List.of(ConsultationStatus.ENDED.name(),
                ConsultationStatus.COMPLETED.name(),
                ConsultationStatus.NO_SHOW.name(),
                ConsultationStatus.CANCELLED.name());
        var requiredStatusesBeforeStarting = List.of(ConsultationStatus.READY.name(), ConsultationStatus.CHECKED_IN.name());
        if (!requiredStatusesBeforeStarting.contains(consultation.getConsultationStatus().name())) {
            if (statusesIndicatingEndOfConsultation.contains(consultation.getConsultationStatus().name())) {
                throw new ConflictException("This consultation has already ended");
            }
            if (consultation.getConsultationStatus() == ConsultationStatus.IN_PROGRESS) {
                throw new ConflictException("This consultation is in progress");
            }
            throw new AppBadRequestException("The patient has not yet checked in or the patient is not yet ready");
        }
        // The Consultation should not be started 15 minutes before the actual appointed time
        if ((env.matchesProfiles("test") || env.matchesProfiles("prod"))
                && appointment.getStartDateAndTime()
                .minusMinutes(MINUTES_BEFORE_APPOINTED_TIME_FOR_CONSULTATION_TO_START)
                .isAfter(dateTimeOfRequest)) {
            throw new ConsultationStartedBeforeAppointmentException();
        }
        ConsultationSession consultationSession = new ConsultationSession(appointment.getPatient(),
                appointment.getConsultant(),
                consultation);
        consultation.setSession(consultationSession);
        consultation.setConsultationStatus(ConsultationStatus.IN_PROGRESS);
        consultation.setStartedAt(LocalDateTime.now());
        consultation = consultationRepository.save(consultation);
        consultationSessionRepository.save(consultationSession);
        consultationSSEService.notifyConsultationStarted(appointment.getPatient().getUserId(), consultationId);
        return getStartResponse(appointment.getConsultant(), consultation);
    }

    @NonNull
    private StartResponse getStartResponse(Consultant consultant, Consultation consultation) {
        String videoCallToken = streamVideoService.createVideoCallToken(consultant.getUserId());
        String callId = consultation.getConsultationId();
        String apiKey = streamVideoService.getApiKey();
        StartResponse.User user = new StartResponse.User(consultant.getUserId(),
                consultant.getProfile().getUserName().getFullName());
        return new StartResponse(callId, videoCallToken, apiKey, user);
    }

    @Transactional
    public ConsultationResponse completeConsultation(ConsultationCompleteRequest completeRequest) {
        var consultation = consultationRepository.findByConsultationId(completeRequest.consultationId())
                .orElseThrow(() -> new EntityNotFoundException("Consultation with id: %s either doesn't exist or has been deleted"));
        consultation.setConsultationStatus(ConsultationStatus.ENDED);
        consultation.getAppointment().setStatus(AppointmentStatus.COMPLETED);
        consultation.setEndedAt(LocalDateTime.now());
        var event = new ConsultationEndedWithoutFollowUpData(
                consultation.getConsultationId(),
                consultation.getAppointment().getPatient().getUserId(),
                consultation.getAppointment().getConsultant().getUserId()
        );
        applicationEventPublisher.publishEvent(event);

        consultationSSEService.notifyConsultationEnded(consultation.getAppointment().getPatient().getUserId(),
                consultation.getConsultationId());
        return consultationMapper.consultationToConsultationResponseWithPatientData(consultation,
                consultation.getAppointment().getPatient(),
                consultation.getAppointment().getConsultant());
    }

    private void verifyConsultationNotStartedOrEnded (Consultation consultation) {
        var consultationStarted = List.of(ConsultationStatus.CHECKED_IN,
                ConsultationStatus.READY,
                ConsultationStatus.IN_PROGRESS).contains(consultation.getConsultationStatus());
        if (consultationStarted) {
            throw new ConflictException("Consultation already started");
        }
        if (consultation.getConsultationStatus().equals(ConsultationStatus.NO_SHOW)
                || consultation.getConsultationStatus().equals(ConsultationStatus.COMPLETED)) {
            throw new AppBadRequestException("Consultation has already ended");
        }
    }

    @Transactional
    public JoinResponse joinConsultation(String consultationId) {
        UserDTO userDTO = authUtils.getCurrentUser();
        Consultation consultation = consultationRepository.findByConsultationId(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation not found"));
        verifyConsultationNotStartedOrEnded(consultation);
        Patient patient = consultation.getAppointment().getPatient();
        if (!userDTO.userId().equals(patient.getUserId())) {
            throw new AuthorizationException("You cannot join this consultation because you weren't registered with it");
        }
        String videoCallToken = streamVideoService.createVideoCallToken(patient.getUserId());
        String callId = consultation.getConsultationId();
        JoinResponse.User user = new JoinResponse.User(patient.getUserId(), patient.getPatientProfile().getUserName().getFullName());
        consultationSSEService.notifyPatientJoined(
                consultation.getAppointment().getConsultant().getUserId(),
                consultation.getConsultationId(),
                patient.getUserId()
        );
        consultation.setConsultationStatus(ConsultationStatus.READY);
        return new JoinResponse("", videoCallToken, callId, user);
    }

    @Transactional
    public CheckInResponse checkIn(String consultationId) {
        UserDTO userDTO = authUtils.getCurrentUser();
        Consultation consultation = consultationRepository.findByConsultationId(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation not found"));
       verifyConsultationNotStartedOrEnded(consultation);
        Patient patient = consultation.getAppointment().getPatient();
        if (!userDTO.userId().equals(patient.getUserId())) {
            throw new AuthorizationException("You cannot join this consultation because you weren't registered with it");
        }
        consultationSSEService.notifyPatientJoined(
                consultation.getAppointment().getConsultant().getUserId(),
                consultation.getConsultationId(),
                patient.getUserId()
        );
        consultation.setConsultationStatus(ConsultationStatus.CHECKED_IN);
        consultation.setCheckedInAt(LocalDateTime.now());
        return new CheckInResponse(consultation.getCheckedInAt().toString());
    }

    @Transactional(readOnly = true)
    public Set<ConsultationProjection> retrieveConsultationDetailByPatientId(String patientId) {
        return consultationRepository.findConsultationByPatientId(patientId);
    }

    @Transactional(readOnly = true)
    public Set<ConsultationProjection> retrieveConsultationDetailByConsultantId(String consultantId) {
        return consultationRepository.findConsultationByConsultantId(consultantId);
    }

    @Transactional(readOnly = true)
    public Set<ConsultationProjection> retrieveConsultationsByPatientIdToday(String patientId){
        return consultationRepository.findConsultationByPatientIdForDate(patientId, LocalDate.now());
    }
    @Transactional(readOnly = true)
    public Set<ConsultationProjection> retrieveConsultationsByConsultantIdToday(String consultantId){
        return consultationRepository.findConsultationByConsultantIdForDate(consultantId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public PageResponse<ConsultationResponse> retrieveConsultationPaged(Pageable pageable) {
        var currentUser = authUtils.getCurrentUser();
        Page<ConsultationResponse> consultationResponse = Page.empty();
        if (currentUser.userType() == UserType.PATIENT) {
            consultationResponse = consultationCustomRepository.getConsultationsByPatientIdPaged(currentUser.userId(), pageable);
        } else if (currentUser.userType() == UserType.CONSULTANT) {
            consultationResponse =
                    consultationCustomRepository.getConsultationsByConsultationIdPaged(currentUser.userId(), pageable);
        }
        return PageResponse.from(consultationResponse);

    }

    public Consultation getConsultationById(String consultationId) {
        return consultationRepository.findByConsultationId(consultationId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Consultation with id: %s not found".formatted(consultationId))
                );
    }

    public Set<PatientMedicalData> getMedicalDataFromOngoingConsultations() {
        return consultationCustomRepository.getPatientMedicalDataFromOngoingConsultations();
    }

    public Set<ReviewDTO> retrieveReviewsByConsultantId(String consultantId) {
        return consultationRepository.findReviewsForConsultant(consultantId);
    }

    @Transactional(readOnly = true)
    public ConsultationResponse getConsultationByConsultationId(String id) {
        Consultation consultation = consultationRepository.findByConsultationId(id)
                .orElseThrow(() -> new EntityNotFoundException("Consultation not found"));
        Appointment appointment = consultation.getAppointment();
        Patient patient = appointment.getPatient();
        Consultant consultant = appointment.getConsultant();
        return consultationMapper.consultationToConsultationResponseWithPatientData(consultation,
                patient,
                consultant);
    }

    public long countOfCompletedConsultationsFromAppointmentIds(Set<String> consultationIds) {
        if (consultationIds.isEmpty()) return 0;

        return consultationRepository.countAllConsultationByConsultationIdsAndStatus(consultationIds, ConsultationStatus.COMPLETED);
    }

    public Long getCountConsultationsByConsultantIdAndDate(String consultantId, LocalDate date) {
        return consultationRepository.countAllConsultationsByConsultantIdForDate(consultantId, date);
    }
}
