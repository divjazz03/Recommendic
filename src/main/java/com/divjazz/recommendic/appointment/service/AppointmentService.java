package com.divjazz.recommendic.appointment.service;

import com.divjazz.recommendic.appointment.controller.payload.AppointmentCreationRequest;
import com.divjazz.recommendic.appointment.controller.payload.AppointmentCreationResponse;
import com.divjazz.recommendic.appointment.enums.AppointmentEventType;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.event.AppointmentEvent;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.AppointmentRepository;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.global.exception.AuthorizationException;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final PatientRepository patientRepository;
    private final AuthUtils authUtils;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Appointment getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment with id: %s not found".formatted(appointmentId)));
    }

    @Transactional
    public AppointmentCreationResponse createAppointment(AppointmentCreationRequest appointmentCreationRequest) {

        Schedule schedule = scheduleRepository.findByScheduleId(appointmentCreationRequest.scheduleId())
                .orElseThrow(() -> new EntityNotFoundException("Schedule was not found or doesn't exist"));
        Patient patient = (Patient) authUtils.getCurrentUser();

        Consultant consultant = schedule.getConsultant();
        Appointment appointment = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .schedule(schedule)
                .status(AppointmentStatus.REQUESTED)
                .appointmentDate(getADateFromSchedule(schedule))
                .consultationChannel(ConsultationChannel.valueOf(appointmentCreationRequest.channel().toUpperCase()))
                .build();

        appointment = appointmentRepository.save(appointment);
        AppointmentEvent appointmentEvent = new AppointmentEvent(
                AppointmentEventType.APPOINTMENT_REQUESTED,
                Map.of("name", patient.getPatientProfile().getUserName().getFullName(),
                        "subjectId", appointment.getId(),
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
    public void confirmAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        if (!authUtils.getCurrentUser().getUserId().equals(appointment.getConsultant().getUserId())) {
            throw new AuthorizationException("You do not have authority to confirm this appointment");
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        AppointmentEvent appointmentEvent = new AppointmentEvent(
                AppointmentEventType.APPOINTMENT_CONFIRMED,
                Map.of("name", appointment.getConsultant().getProfile().getUserName().getFullName(),
                        "subjectId", appointment.getId(),
                        "targetId", appointment.getPatient().getUserId(),
                        "startDateTime", appointment.getStartDateAndTime().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                        "endDateTime", appointment.getEndDateAndTime().format(DateTimeFormatter.RFC_1123_DATE_TIME))
        );
        applicationEventPublisher.publishEvent(appointmentEvent);
    }

    @Transactional
    public void cancelAppointment(long id, String reason) {
        var appointment = appointmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Appointment does not exist or has been deleted"));
        var userIsAuthorizedToCancel = authUtils.getCurrentUser().getUserId().equals(appointment.getPatient().getUserId());
        if (!userIsAuthorizedToCancel) {
            throw new AuthorizationException("You do not have authority to cancel this appointment");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        AppointmentEvent appointmentEvent = new AppointmentEvent(
                AppointmentEventType.APPOINTMENT_CANCELLED,
                Map.of("targetId", appointment.getConsultant().getUserId(),
                        "subjectId", appointment.getId(),
                        "reason", reason,
                        "name", ((Patient) authUtils.getCurrentUser()).getPatientProfile().getUserName().getFullName(),
                        "startDateTime", appointment.getStartDateAndTime().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                        "endDateTime", appointment.getEndDateAndTime().format(DateTimeFormatter.RFC_1123_DATE_TIME))
                );
        applicationEventPublisher.publishEvent(appointmentEvent);
    }

    public Stream<Appointment> getAppointmentsByPatientId(String patientId) {
        return appointmentRepository.findAppointmentsByPatient_UserId(patientId);
    }


    public Stream<Appointment> getAppointmentsByConsultantId(String consultantId) {
        return appointmentRepository.findAppointmentsByConsultant_UserId(consultantId);
    }

    public List<String> getAppointmentDatesAndTimeForSchedule(Schedule schedule) {
        return appointmentRepository.findAppointmentsBySchedule(schedule).stream().map(
                appointment -> appointment.getStartDateAndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        ).toList();
    }

    private LocalDate getADateFromWeeklySchedule(Schedule schedule) {
        Set<String> daysOfWeek = schedule.getRecurrenceRule().weekDays();
        LocalDate localDate = appointmentRepository.findLatestAppointmentDateForTheSchedule(schedule.getId());
        if (Objects.nonNull(localDate)) {
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                if (daysOfWeek.contains(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase(Locale.ENGLISH))) {
                    while (localDate.getDayOfWeek() != dayOfWeek) {
                        localDate = localDate.plusDays(1);
                    }
                }
            }
            return localDate;
        } else {
            LocalDate localDate1 = LocalDate.now().plusDays(1);
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                if (daysOfWeek.contains(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase(Locale.ENGLISH))) {
                    while (localDate1.getDayOfWeek() != dayOfWeek) {
                        localDate1 = localDate1.plusDays(1);
                    }
                }
            }
            return localDate1;
        }
    }

    private LocalDate getADateFromMonthlySchedule(Schedule schedule) {
        LocalDate localDate = appointmentRepository.findLatestAppointmentDateForTheSchedule(schedule.getId());
        if (Objects.nonNull(localDate)) {
            return localDate.plusMonths(1);
        }
        return LocalDate.now().plusDays(1);

    }

    private LocalDate getADateFromDailySchedule(Schedule schedule) {
        LocalDate localDate = appointmentRepository.findLatestAppointmentDateForTheSchedule(schedule.getId());
        if (Objects.nonNull(localDate)) {
                return localDate.plusDays(1);
        }
        return LocalDate.now().plusDays(1);

    }


    private LocalDate getADateFromSchedule(Schedule schedule) {
            return switch (schedule.getRecurrenceRule().frequency()) {
                case ONE_OFF, DAILY -> getADateFromDailySchedule(schedule);
                case WEEKLY -> getADateFromWeeklySchedule(schedule);
                case MONTHLY -> getADateFromMonthlySchedule(schedule);
            };
        }
}
