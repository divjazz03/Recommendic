package com.divjazz.recommendic.appointment.service;

import com.divjazz.recommendic.appointment.controller.payload.*;
import com.divjazz.recommendic.appointment.enums.AppointmentHistory;
import com.divjazz.recommendic.appointment.event.*;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.exception.AppointmentBookedException;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.AppointmentCustomRepository;
import com.divjazz.recommendic.appointment.repository.AppointmentRepository;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.appointment.repository.projection.AppointmentProjection;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.global.exception.AuthorizationException;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.ResponseWithCount;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final PatientRepository patientRepository;
    private final AuthUtils authUtils;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AppointmentCustomRepository appointmentCustomRepository;

    public Appointment getAppointmentByAppointmentId(String appointmentId) {
        return appointmentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment with id: %s not found".formatted(appointmentId)));
    }

    public ResponseWithCount<? extends AppointmentResponse> getAppointmentsForThisUser(Pageable pageable) {
        var authUser = authUtils.getCurrentUser();

        return switch (authUser.userType()) {
            case ADMIN -> null;
            case CONSULTANT -> getAppointmentForConsultantUser(authUser, pageable);
            case PATIENT -> getAppointmentForPatientUser(authUser, pageable);
        };

    }

    private ResponseWithCount<PatientAppointmentResponse> getAppointmentForPatientUser(UserDTO authUser, Pageable pageable) {

        var result = appointmentCustomRepository.retrievePatientAppointments(authUser.id(), pageable);
        var elements = result.elements().stream().map(dto ->
                new PatientAppointmentResponse(
                        dto.id(),
                        dto.consultantId(),
                        dto.doctorName(),
                        dto.specialty(),
                        dto.date(),
                        dto.time(),
                        dto.duration(),
                        dto.type(),
                        dto.location(),
                        dto.phone(),
                        dto.status(),
                        dto.notes(),
                        dto.reason(),
                        dto.preparation()
                )).collect(Collectors.toSet());
        return new ResponseWithCount<>(elements, result.total());
    }

    private ResponseWithCount<ConsultantAppointmentResponse> getAppointmentForConsultantUser(UserDTO authUser, Pageable pageable) {
        var result = appointmentCustomRepository.retrieveConsultantAppointments(authUser.id(), pageable);
        var elements = result.elements().stream().map(dto ->
                new ConsultantAppointmentResponse(
                        dto.id(),
                        dto.patientName(),
                        dto.patientAge(),
                        dto.patientPhone(),
                        dto.patientEmail(),
                        dto.date(),
                        dto.time(),
                        dto.duration(),
                        dto.type(),
                        dto.location(),
                        dto.reason(),
                        dto.symptoms(),
                        dto.medicalHistory(),
                        dto.requestedDate(),
                        dto.priority(),
                        dto.notes(),
                        dto.cancellationReason(),
                        dto.status()
                )).collect(Collectors.toSet());
        return new ResponseWithCount<>(elements,result.total());
    }


    @Transactional
    public AppointmentCreationResponse createAppointment(AppointmentCreationRequest appointmentCreationRequest) {
        Schedule schedule = scheduleRepository.findByScheduleId(appointmentCreationRequest.scheduleId())
                .orElseThrow(() -> new EntityNotFoundException("Schedule was not found or doesn't exist"));
        UserDTO userDTO = authUtils.getCurrentUser();
        Patient patient = patientRepository.getReferenceById(userDTO.id());
        boolean appointmentExists = appointmentRepository.existsByAppointmentDateAndSchedule_Id(LocalDate.parse(appointmentCreationRequest.date()), schedule.getId());
        if (appointmentExists) {
            throw new AppointmentBookedException("The schedule at this time for this day has already been booked");
        }
        Consultant consultant = schedule.getConsultant();
        AppointmentHistory history = appointmentCreationRequest.history() == null
                || appointmentCreationRequest.history() == AppointmentHistory.NEW
                ? AppointmentHistory.NEW
                : AppointmentHistory.FOLLOW_UP;

        Appointment appointment = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .schedule(schedule)
                .status(AppointmentStatus.PENDING)
                .appointmentDate(LocalDate.parse(appointmentCreationRequest.date()))
                .consultationChannel(ConsultationChannel.valueOf(appointmentCreationRequest.channel().toUpperCase()))
                .history(history)
                .build();

        appointment.setReason(appointmentCreationRequest.reason());


        appointment = appointmentRepository.save(appointment);
        var appointmentEvent = new AppointmentRequestedData(
                appointment.getAppointmentId(),
                patient.getUserId(),
                patient.getPatientProfile().getUserName().getFullName(),
                consultant.getProfile().getUserName().getFullName(),
                appointment.getConsultant().getUserId(),
                appointment.getStartDateAndTime().format(DateTimeFormatter.ISO_DATE_TIME),
                appointment.getEndDateAndTime().format(DateTimeFormatter.ISO_DATE_TIME)
        );
        applicationEventPublisher.publishEvent(appointmentEvent);
        return new AppointmentCreationResponse(
                appointment.getAppointmentId(),
                patient.getPatientProfile().getUserName().getFullName(),
                patient.getUserId(),
                consultant.getProfile().getUserName().getFullName(),
                consultant.getUserId(),
                appointment.getStatus().toString(),
                appointment.getStartDateAndTime().toString(),
                appointment.getEndDateAndTime().toString(),
                appointmentCreationRequest.channel(),
                appointment.getHistory()
        );
    }

    @Transactional
    public void confirmAppointment(AppointmentConfirmationRequest confirmationRequest) {
        AppointmentProjection appointment = getAppointmentProjection(confirmationRequest.appointmentId());
        if (!authUtils.getCurrentUser().userId().equals(appointment.getConsultantId())) {
            throw new AuthorizationException("You do not have authority to confirm this appointment");
        }
        appointmentRepository.confirmAppointmentStatusAndNotesByAppointmentId(confirmationRequest.appointmentId(), AppointmentStatus.CONFIRMED, confirmationRequest.note());
        var appointmentEvent = new AppointmentConfirmedData(
                confirmationRequest.appointmentId(),
                appointment.getConsultantId(),
                appointment.getConsultantFullName().getFullName(),
                appointment.getPatientId(),
                appointment.getPatientFullName().getFullName(),
                LocalDateTime.of(appointment.getStartDate(),appointment.getStartTime()).format(DateTimeFormatter.ISO_DATE_TIME),
                LocalDateTime.of(appointment.getEndDate(),appointment.getEndTime()).format(DateTimeFormatter.ISO_DATE_TIME)
        );

        applicationEventPublisher.publishEvent(appointmentEvent);
    }



    @Transactional
    public void cancelAppointment(AppointmentCancellationRequest cancellationRequest) {
        var currentUser = authUtils.getCurrentUser();
        AppointmentProjection appointment = getAppointmentProjection(cancellationRequest.appointmentId());
        var userIsAuthorizedToCancel = currentUser.userId().equals(appointment.getPatientId());
        if (!userIsAuthorizedToCancel) {
            throw new AuthorizationException("You do not have authority to cancel this appointment");
        }
        appointmentRepository.updateAppointmentStatusByAppointmentId(cancellationRequest.appointmentId(), AppointmentStatus.CANCELLED);
        var appointmentEvent = new AppointmentCancelledData(
                cancellationRequest.appointmentId(),
                appointment.getPatientId(),
                appointment.getConsultantId(),
                appointment.getPatientFullName().getFullName(),
                appointment.getConsultantFullName().getFullName(),
                LocalDateTime.of(appointment.getStartDate(),appointment.getStartTime()).format(DateTimeFormatter.ISO_DATE_TIME),
                LocalDateTime.of(appointment.getEndDate(),appointment.getEndTime()).format(DateTimeFormatter.ISO_DATE_TIME),
                cancellationRequest.reason()
        );
        applicationEventPublisher.publishEvent(appointmentEvent);
    }

    @Transactional
    public void rescheduleRequest(AppointmentRescheduleRequest rescheduleRequest) {
        var currentUser = authUtils.getCurrentUser();
        var appointmentProjection = getAppointmentProjection(rescheduleRequest.appointmentId());
        var userIsAuthorizedToReschedule = currentUser.userId().equals(appointmentProjection.getPatientId())
                || currentUser.userId().equals(appointmentProjection.getConsultantId());
        if (!userIsAuthorizedToReschedule) {
            throw new AuthorizationException("You do not have the authority to reschedule this appointment");
        }

        appointmentRepository.updateAppointmentDate(rescheduleRequest.appointmentId(), LocalDate.parse(rescheduleRequest.newDate()));
    }

    private AppointmentProjection getAppointmentProjection(String appointmentId) {
        return appointmentRepository.findAppointmentByAppointmentId(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment does not exist"));
    }



    public Stream<Appointment> getAppointmentsByPatientId(String patientId) {
        return appointmentRepository.findAppointmentsByPatient_UserId(patientId);
    }

    public Set<Appointment> getTodayAppointmentByPatientId(String patientId) {
        return appointmentRepository.findAllByPatient_UserIdAndAppointmentDate(patientId, LocalDate.now());
    }

    public Set<Appointment> getTodayAppointmentByConsultantId(String consultantId) {
        return appointmentRepository.findAllByConsultant_UserIdAndAppointmentDateAndStatus(consultantId, LocalDate.now(), AppointmentStatus.CONFIRMED);
    }

    public int getCountAppointmentByConsultantIdAndDate(String consultantId, LocalDate date) {
        return appointmentRepository.countAllByConsultant_UserIdAndAppointmentDate(consultantId, LocalDate.now());
    }


    public Stream<Appointment> getAppointmentsByConsultantId(String consultantId) {
        return appointmentRepository.findAppointmentsByConsultant_UserId(consultantId);
    }

    public List<String> getAppointmentDatesAndTimeForSchedule(Schedule schedule) {
        return appointmentRepository.findAppointmentsBySchedule(schedule).stream()
                .map(appointment -> appointment.getStartDateAndTime().format(DateTimeFormatter.ISO_DATE_TIME)).toList();
    }

    public List<String> getAppointmentDatesAndTimeForScheduleAndDate(Schedule schedule, LocalDate date) {
        return appointmentRepository.findAppointmentByScheduleAndAppointmentDate(schedule, date).stream()
                .map(appointment -> appointment.getStartDateAndTime().format(DateTimeFormatter.ISO_DATE_TIME))
                .toList();
    }

}
