package com.divjazz.recommendic.appointment.service;

import com.divjazz.recommendic.appointment.domain.Availability;
import com.divjazz.recommendic.appointment.domain.RecurrenceFrequency;
import com.divjazz.recommendic.appointment.domain.Slot;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.AppointmentRepository;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.global.exception.AppBadRequestException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvailabilityService {


    private final ScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;

    public Set<Slot> getAvailableSlotsByDateAndConsultantId(String startDate, String consultantId) {
        LocalDateTime startDateTime;
        try{
            startDateTime = LocalDateTime.of(LocalDate.parse(startDate), LocalTime.now());
        } catch (DateTimeParseException e) {
            throw new AppBadRequestException("Illegal date string %s".formatted(startDate));
        }
        return getSlots(consultantId, startDateTime);
    }


    public Set<Slot> getTodayAvailableSlots(String consultantId) {
        var startDate = LocalDateTime.now();
        return getSlots(consultantId, startDate);

    }

    @NotNull
    private Set<Slot> getSlots(String consultantId, LocalDateTime startDate) {
        Set<Schedule> consultantSchedules = scheduleRepository.findAllByConsultant_UserId(consultantId);
        Set<Slot> bookedSlots = appointmentRepository
                .findAllByConsultant_UserIdAndAppointmentDate(consultantId,
                        startDate.toLocalDate())
                .stream()
                .map(appointment -> new Slot(appointment.getSchedule().getScheduleId(), appointment.getStartDateAndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
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
        var startDate = LocalDateTime.now();
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
                        var parseSlot = LocalDateTime.parse(slot.dateTime());
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

    private Stream<Slot> generateTimeSlots(Schedule schedule, LocalDateTime startDate, LocalDateTime endOfThisWeek) {
        Set<LocalDateTime> localDateTimes = new TreeSet<>();
        switch (schedule.getRecurrenceRule().frequency()) {
            case ONE_OFF -> {
                var currentDay = startDate.truncatedTo(ChronoUnit.DAYS);

                while (!currentDay.isAfter(endOfThisWeek)) {
                    if (Objects.nonNull(schedule.getRecurrenceRule().endDate())) {
                        var dateToCompare = LocalDate.parse(schedule.getRecurrenceRule().endDate());
                        if (currentDay.toLocalDate().isEqual(dateToCompare)){
                            var slot = currentDay
                                    .withHour(schedule.getStartTime().getHour())
                                    .withMinute(schedule.getStartTime().getMinute());
                            localDateTimes.add(slot);
                        }
                        currentDay = currentDay.plusDays(1);
                    } else{
                        throw new IllegalStateException("A daily schedule of id %s without an end date");
                    }
                }
            }
            case DAILY -> {
                var currentDay = startDate.truncatedTo(ChronoUnit.DAYS);

                while (!currentDay.isAfter(endOfThisWeek)) {
                    var slot = currentDay
                            .withHour(schedule.getStartTime().getHour())
                            .withMinute(schedule.getStartTime().getMinute());
                    localDateTimes.add(slot);
                    currentDay = currentDay.plusDays(1);
                }
            }
            case MONTHLY -> {
                log.warn("Monthly recurrence not yet implemented");
            }
            case WEEKLY -> {
                var scheduleWeek = schedule.getRecurrenceRule().weekDays();
                for (String weekDay : scheduleWeek) {
                    var slot = startDate
                            .with(TemporalAdjusters.next(DayOfWeek.valueOf(weekDay.toUpperCase())))
                            .withHour(schedule.getStartTime().getHour())
                            .withMinute(schedule.getStartTime().getMinute());
                    if (schedule.getRecurrenceRule().frequency().equals(RecurrenceFrequency.WEEKLY)){
                        localDateTimes.add(slot);
                    }

                }
            }
        }
        return localDateTimes
                .stream()
                .map(dateTime -> new Slot(schedule.getScheduleId(), dateTime.format(DateTimeFormatter.ISO_DATE_TIME)))
                .distinct();
    }
}
