package com.divjazz.recommendic.appointment.service;

import com.divjazz.recommendic.appointment.controller.payload.AppointmentCreationRequest;
import com.divjazz.recommendic.appointment.controller.payload.AppointmentCreationResponse;
import com.divjazz.recommendic.appointment.domain.Availability;
import com.divjazz.recommendic.appointment.domain.Slot;
import com.divjazz.recommendic.appointment.dto.AppointmentDTO;
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
import com.divjazz.recommendic.user.repository.PatientCustomRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {
    private static final int HOURS_TO_SKIP = 12;
    private static final int NINE_PM_IN_24HOURS = 21;
    private static final int NINE_AM_IN_24HOURS = 9;
    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final PatientRepository patientRepository;
    private final PatientCustomRepository patientCustomRepository;
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
        AppointmentProjection appointment = appointmentRepository.findAppointmentByAppointmentId(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment does not exist"));
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
    public void cancelAppointment(String appointmentId, String reason) {
        var currentUser = authUtils.getCurrentUser();
        AppointmentProjection appointment = appointmentRepository.findAppointmentByAppointmentId(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment does not exist"));
        var userIsAuthorizedToCancel = currentUser.userId().equals(appointment.getPatientId());
        if (!userIsAuthorizedToCancel) {
            throw new AuthorizationException("You do not have authority to cancel this appointment");
        }
        appointmentRepository.updateAppointmentStatusByAppointmentId(appointmentId, AppointmentStatus.CANCELLED);
        AppointmentEvent appointmentEvent = new AppointmentEvent(
                AppointmentEventType.APPOINTMENT_CANCELLED,
                Map.of("targetId", appointment.getConsultantId(),
                        "subjectId", appointmentId,
                        "reason", reason,
                        "name", appointment.getPatientFullName().getFullName(),
                        "startDateTime", OffsetDateTime.of(appointment.getStartDate(), appointment.getStartTime(), appointment.getOffset()).toString(),
                        "endDateTime", OffsetDateTime.of(appointment.getEndDate(), appointment.getEndTime(), appointment.getOffset()).toString())
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
        return appointmentRepository.findAppointmentsBySchedule(schedule).stream()
                .map(appointment -> appointment.getStartDateAndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).toList();
    }

    public List<String> getAppointmentDatesAndTimeForScheduleAndDate(Schedule schedule, LocalDate date) {
        return appointmentRepository.findAppointmentByScheduleAndAppointmentDate(schedule, date).stream()
                .map(appointment -> appointment.getStartDateAndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .toList();
    }

    /*
     * Finds the Availability of a consultant within a month time
     * */

    public Set<Slot> getTodayAvailableSlots(String consultantId) {
        var startDate = OffsetDateTime.now();
        Set<Schedule> consultantSchedules = scheduleRepository.findAllByConsultant_UserId(consultantId);
        Set<Slot> bookedSlots = appointmentRepository
                .findAllByConsultant_UserIdAndAppointmentDate(consultantId,
                        startDate.toLocalDate())
                .stream()
                .map(appointment -> new Slot(appointment.getSchedule().getScheduleId(), appointment.getStartDateAndTime().toString()))
                .collect(Collectors.toSet());

        Set<Slot> todaySlots = new TreeSet<>();
        for (var schedule : consultantSchedules) {
            generateTimeSlots(schedule, startDate, startDate.withHour(23).withMinute(59).withSecond(59))
                    .filter(slot -> !bookedSlots.contains(slot))
                    .forEach(todaySlots::add);
        }
        return todaySlots;

    }

    @Transactional(readOnly = true)
    public Availability getConsultantAvailability(String consultantId) {
        var startDate = OffsetDateTime.now();
        var endOfThisWeek = startDate.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));

        Set<Schedule> consultantsSchedules = scheduleRepository.findAllByConsultant_UserId(consultantId);
        Set<Slot> bookedSlots = appointmentRepository
                .findAllByConsultant_UserIdAndAppointmentDateBetween(consultantId,
                        startDate.toLocalDate(),
                        endOfThisWeek.toLocalDate())
                .stream()
                .map(appointment -> new Slot(appointment.getSchedule().getScheduleId(), appointment.getStartDateAndTime().toString()))
                .collect(Collectors.toSet());

        Set<Slot> today = new TreeSet<>();
        Set<Slot> tomorrow = new TreeSet<>();
        Set<Slot> thisWeek = new TreeSet<>();
        for (var schedule : consultantsSchedules) {
            generateTimeSlots(schedule, startDate, endOfThisWeek)
                    .filter(slot -> !bookedSlots.contains(slot))
                    .forEach(slot -> {
                        var parseSlot = OffsetDateTime.parse(slot.dateTime());
                        if (parseSlot.isEqual(startDate)) {
                            today.add(slot);
                        } else if (parseSlot.toLocalDate().isEqual(startDate.toLocalDate().plusDays(1))) {
                            tomorrow.add(slot);
                        } else {
                            thisWeek.add(slot);
                        }
                    });
        }
        return new Availability(
                today,
                tomorrow,
                thisWeek,
                bookedSlots

        );


    }

    private Stream<Slot> generateTimeSlots(Schedule schedule, OffsetDateTime startDate, OffsetDateTime endOfThisWeek) {
        Set<OffsetDateTime> offsetDateTimes = new TreeSet<>();
        switch (schedule.getRecurrenceRule().frequency()) {
            case ONE_OFF, DAILY -> {
                var currentDay = startDate.truncatedTo(ChronoUnit.DAYS);

                while (!currentDay.isAfter(endOfThisWeek)) {
                    var slot = currentDay
                            .withHour(schedule.getStartTime().getHour())
                            .withMinute(schedule.getStartTime().getMinute());

                    offsetDateTimes.add(slot);
                    currentDay = currentDay.plusDays(1);
                }
            }
            case MONTHLY -> {
                log.warn("Monthly recurrence no yet implemented");
            }

            case WEEKLY -> {
                var scheduleWeek = schedule.getRecurrenceRule().weekDays();
                for (String weekDay : scheduleWeek) {
                    var slot = startDate
                            .with(TemporalAdjusters.next(DayOfWeek.valueOf(weekDay.toUpperCase())))
                            .withHour(schedule.getStartTime().getHour())
                            .withMinute(schedule.getStartTime().getMinute());
                    offsetDateTimes.add(slot);

                }
            }
            case null, default -> throw new IllegalStateException("schedule recurrence frequency should not be null");
        }
        return offsetDateTimes.stream().map(dateTime -> new Slot(schedule.getScheduleId(), dateTime.toString()));
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
