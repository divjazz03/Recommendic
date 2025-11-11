package com.divjazz.recommendic.appointment.service;

import com.divjazz.recommendic.appointment.controller.payload.AppointmentCancellationRequest;
import com.divjazz.recommendic.appointment.controller.payload.AppointmentCreationRequest;
import com.divjazz.recommendic.appointment.controller.payload.AppointmentCreationResponse;
import com.divjazz.recommendic.appointment.controller.payload.AppointmentRescheduleRequest;
import com.divjazz.recommendic.appointment.enums.AppointmentEventType;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.event.AppointmentEvent;
import com.divjazz.recommendic.appointment.exception.AppointmentBookedException;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.AppointmentRepository;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.appointment.repository.projection.AppointmentProjection;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.global.exception.AuthorizationException;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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

    public Appointment getAppointmentByAppointmentId(String appointmentId) {
        return appointmentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment with id: %s not found".formatted(appointmentId)));
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
        Appointment appointment = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .schedule(schedule)
                .status(AppointmentStatus.REQUESTED)
                .appointmentDate(LocalDate.parse(appointmentCreationRequest.date()))
                .consultationChannel(ConsultationChannel.valueOf(appointmentCreationRequest.channel().toUpperCase()))
                .build();

        appointment.setReason(appointmentCreationRequest.reason());


        appointment = appointmentRepository.save(appointment);
        AppointmentEvent appointmentEvent = new AppointmentEvent(
                AppointmentEventType.APPOINTMENT_REQUESTED,
                Map.of("name", patient.getPatientProfile().getUserName().getFullName(),
                        "subjectId", appointment.getAppointmentId(),
                        "targetId", consultant.getUserId(),
                        "startDateTime", appointment.getStartDateAndTime().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                        "endDateTime", appointment.getEndDateAndTime().format(DateTimeFormatter.RFC_1123_DATE_TIME))
        );
        applicationEventPublisher.publishEvent(appointmentEvent);
        return new AppointmentCreationResponse(
                patient.getPatientProfile().getUserName().getFullName(),
                consultant.getProfile().getUserName().getFullName(),
                appointment.getStatus().toString(),
                appointment.getStartDateAndTime().toString(),
                appointment.getEndDateAndTime().toString(),
                appointmentCreationRequest.channel()
        );
    }

    @Transactional
    public void confirmAppointment(String appointmentId) {
        AppointmentProjection appointment = getAppointmentProjection(appointmentId);
        if (!authUtils.getCurrentUser().userId().equals(appointment.getConsultantId())) {
            throw new AuthorizationException("You do not have authority to confirm this appointment");
        }
        appointmentRepository.updateAppointmentStatusByAppointmentId(appointmentId, AppointmentStatus.CONFIRMED);
        AppointmentEvent appointmentEvent = new AppointmentEvent(
                AppointmentEventType.APPOINTMENT_CONFIRMED,
                Map.of("name", appointment.getConsultantFullName().getFullName(),
                        "subjectId", appointmentId,
                        "targetId", appointment.getPatientId(),
                        "startDateTime", OffsetDateTime.of(appointment.getStartDate(), appointment.getStartTime(), appointment.getOffset()).toString(),
                        "endDateTime", OffsetDateTime.of(appointment.getEndDate(), appointment.getEndTime(), appointment.getOffset()).toString()
                )
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
        AppointmentEvent appointmentEvent = new AppointmentEvent(
                AppointmentEventType.APPOINTMENT_CANCELLED,
                Map.of("targetId", appointment.getConsultantId(),
                        "subjectId", cancellationRequest.appointmentId(),
                        "reason", cancellationRequest.reason(),
                        "name", appointment.getPatientFullName().getFullName(),
                        "startDateTime", OffsetDateTime.of(appointment.getStartDate(), appointment.getStartTime(), appointment.getOffset()).toString(),
                        "endDateTime", OffsetDateTime.of(appointment.getEndDate(), appointment.getEndTime(), appointment.getOffset()).toString())
        );
        applicationEventPublisher.publishEvent(appointmentEvent);
    }

    public void rescheduleRequest (AppointmentRescheduleRequest rescheduleRequest) {
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


    public Stream<Appointment> getAppointmentsByConsultantId(String consultantId) {
        return appointmentRepository.findAppointmentsByConsultant_UserId(consultantId);
    }

    public List<String> getAppointmentDatesAndTimeForSchedule(Schedule schedule) {
        return appointmentRepository.findAppointmentsBySchedule(schedule).stream()
                .map(appointment -> appointment.getStartDateAndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).toList();
    }

    public List<String> getAppointmentDatesAndTimeForScheduleAndDate(Schedule schedule, LocalDate date) {
        return appointmentRepository.findAppointmentByScheduleAndAppointmentDate(schedule, date).stream()
                .map(appointment -> appointment.getStartDateAndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .toList();
    }



}
