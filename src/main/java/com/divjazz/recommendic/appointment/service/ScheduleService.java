package com.divjazz.recommendic.appointment.service;

import com.divjazz.recommendic.appointment.controller.payload.*;
import com.divjazz.recommendic.appointment.domain.RecurrenceFrequency;
import com.divjazz.recommendic.appointment.domain.RecurrenceRule;
import com.divjazz.recommendic.appointment.domain.Slot;
import com.divjazz.recommendic.appointment.dto.ScheduleResponseDTO;
import com.divjazz.recommendic.appointment.dto.ScheduleWithAppointmentDetail;
import com.divjazz.recommendic.appointment.mapper.ScheduleMapper;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.ScheduleCustomRepository;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.global.exception.AppBadRequestException;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.ResponseWithCount;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantStat;
import com.divjazz.recommendic.user.repository.ConsultantStatRepository;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    public final ScheduleCustomRepository scheduleCustomRepository;
    private final ScheduleRepository scheduleRepository;
    private final AuthUtils authUtils;
    private final ConsultantService consultantService;
    private final ConsultantStatRepository consultantStatRepository;
    private final AppointmentService appointmentService;
    private final ObjectMapper objectMapper;
    private final AvailabilityService availabilityService;


    @Transactional
    public ScheduleResponseDTO createSchedule(List<ScheduleCreationRequest> creationRequests) {
        UserDTO userProjection = authUtils.getCurrentUser();
        Consultant consultant = consultantService.getReference(userProjection.id());
        List<Schedule> schedules = creationRequests.stream()
                .map(creationRequest -> {
                    var schedule = Schedule.builder()
                            .name(creationRequest.name())
                            .consultant(consultant)
                            .consultationChannels(toConsultationChannels(creationRequest.channels()))
                            .startTime(LocalTime.parse(creationRequest.startTime(), DateTimeFormatter.ISO_TIME))
                            .endTime(LocalTime.parse(creationRequest.endTime(), DateTimeFormatter.ISO_TIME))
                            .isActive(creationRequest.isActive())
                            .zoneOffset(ZoneOffset.of(creationRequest.zoneOffset()))
                            .build();

                    if (Objects.nonNull(creationRequest.recurrenceRule())) {
                        var recurrenceRule = new RecurrenceRule(
                                RecurrenceFrequency.fromValue(creationRequest.recurrenceRule().frequency()),
                                creationRequest.recurrenceRule().weekDays(),
                                creationRequest.recurrenceRule().interval(),
                                creationRequest.recurrenceRule().endDate()
                        );
                        schedule.setRecurrenceRule(recurrenceRule);
                        verifyScheduleDoesNotConflictWithOthers(recurrenceRule, schedule);
                    }
                    return schedule;

                })
                .toList();

        schedules = scheduleRepository.saveAll(schedules);

        return ScheduleMapper.toScheduleResponseDTO(schedules.getFirst());
    }

    private void verifyScheduleDoesNotConflictWithOthers(RecurrenceRule recurrenceRule, Schedule schedule) {

        var existsByStartTimeAndRecurrenceRuleFrequency = scheduleRepository.existsByStartTimeAndEndTimeAndRecurrenceRule_Frequency(
                schedule.getStartTime().format(DateTimeFormatter.ISO_TIME),
                schedule.getEndTime().format(DateTimeFormatter.ISO_TIME),
                RecurrenceFrequency.DAILY.getValue());
        if (existsByStartTimeAndRecurrenceRuleFrequency) {
            throw new AppBadRequestException("A schedule of frequency %s at time %s already exists".formatted(RecurrenceFrequency.DAILY, schedule.getStartTime()));
        }

        if (recurrenceRule.frequency().equals(RecurrenceFrequency.WEEKLY)) {
            boolean existsByWeekdaysAndStartTime;
            try {
                existsByWeekdaysAndStartTime = scheduleRepository.existsByWeekDaysAndStartTimeAndEndTime(
                        objectMapper.writeValueAsString(recurrenceRule.weekDays()),
                        schedule.getStartTime().format(DateTimeFormatter.ISO_TIME),
                        schedule.getEndTime().format(DateTimeFormatter.ISO_TIME));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if (existsByWeekdaysAndStartTime) {
                throw new AppBadRequestException("A schedule with those weekdays and schedule start time exists");
            }
        }
        if (recurrenceRule.frequency().equals(RecurrenceFrequency.ONE_OFF)) {
            boolean existsWeeklyScheduleWithStartTime = scheduleRepository.existsByStartTimeAndEndTimeAndRecurrenceRule_Frequency(
                    schedule.getStartTime().format(DateTimeFormatter.ISO_TIME),
                    schedule.getEndTime().format(DateTimeFormatter.ISO_TIME),
                    RecurrenceFrequency.WEEKLY.getValue());
            if (existsWeeklyScheduleWithStartTime) {
                throw new AppBadRequestException("A weekly schedule is conflicting with this startTime");
            }

        }
    }

    @Transactional(readOnly = true)
    public ResponseWithCount<ScheduleDisplay> getMySchedules() {
        return scheduleCustomRepository.findAllScheduleDisplaysByConsultantId(authUtils.getCurrentUser().id(), Pageable.ofSize(10));
    }

    public ScheduleResponseDTO getScheduleById(String id) {
        var schedule = scheduleRepository
                .findByScheduleId(id).orElseThrow(() -> new EntityNotFoundException("Schedule with id %s not found".formatted(id)));
        return ScheduleMapper.toScheduleResponseDTO(schedule);
    }

    public ConsultantSchedulesResponse getSchedulesByConsultantIdHandler(String consultantId, String date) {

        ConsultantProfile consultantProfile = consultantService.getConsultantProfileByConsultantId(consultantId);
        ConsultantStat consultantStat = consultantStatRepository
                .findConsultantStatByConsultantId(consultantId).orElse(ConsultantStat.ofEmpty());
        Set<Slot> slots = null;
        if (Objects.nonNull(date)) {
            slots = availabilityService.getAvailableSlotsByDateAndConsultantId(date, consultantId);
        }
        if (Objects.isNull(slots)) {
            slots = Set.of();
        }

        return new ConsultantSchedulesResponse(
                slots,
                new ConsultantSchedulesResponse.ScheduleConsultantProfile(
                        consultantProfile.getUserName().getFullName(),
                        consultantProfile.getTitle(),
                        consultantStat.getRating(),
                        consultantProfile.getProfilePicture().getPictureUrl(),
                        new ConsultationFee(30, 15),
                        consultantProfile.getLocationOfInstitution()
                )
        );

    }

    public List<ScheduleWithAppointmentDetail> getSchedulesByConsultantId(String consultantId) {
        return scheduleRepository.findAllByConsultant_UserId(consultantId)
                .stream()
                .map(schedule -> {
                    var appointmentDateAndTime = appointmentService.getAppointmentDatesAndTimeForSchedule(schedule);
                    return new ScheduleWithAppointmentDetail(
                            ScheduleMapper.toScheduleResponseDTO(schedule),
                            appointmentDateAndTime
                    );
                }).toList();
    }

    public List<ScheduleWithAppointmentDetail> getSchedulesByConsultantIdAndDate(String consultantId, LocalDate date) {
        return scheduleRepository.findAllByConsultant_UserId(consultantId)
                .stream()
                .map(
                        schedule -> {
                            var appointmentDateAndTime = appointmentService.getAppointmentDatesAndTimeForScheduleAndDate(schedule, date);
                            return new ScheduleWithAppointmentDetail(
                                    ScheduleMapper.toScheduleResponseDTO(schedule),
                                    appointmentDateAndTime
                            );
                        }).toList();
    }

    @Transactional
    public ScheduleResponseDTO modifySchedule(String id, ScheduleModificationRequest modificationRequest) {
        log.info("{}", modificationRequest.toString());
        var schedule = scheduleRepository.findByScheduleId(id).orElseThrow(() -> new EntityNotFoundException("Schedule with id %s does not exist or has been deleted".formatted(id)));
        if (!schedule.getConsultant().getUserId().equals(authUtils.getCurrentUser().userId())) {
            throw new AuthorizationDeniedException("You are not the owner of this schedule, hence cannot modify this resource");
        }

        if (Objects.nonNull(modificationRequest.name())) {
            schedule.setName(modificationRequest.name());
        }
        if (Objects.nonNull(modificationRequest.channels()) && !modificationRequest.channels().isEmpty()) {
            schedule.setConsultationChannels(toConsultationChannels(modificationRequest.channels()));
        }
        if (Objects.nonNull(modificationRequest.endTime())) {
            schedule.setEndTime(LocalTime.parse(modificationRequest.endTime()));
        }
        if (Objects.nonNull(modificationRequest.startTime())) {
            schedule.setStartTime(LocalTime.parse(modificationRequest.startTime()));
        }
        if (Objects.nonNull(modificationRequest.zoneOffset())) {
            schedule.setZoneOffset(ZoneOffset.of(modificationRequest.zoneOffset()));
        }
        schedule.setActive(modificationRequest.isActive());
        if (Objects.nonNull(modificationRequest.recurrenceRule())) {
            schedule.setRecurrenceRule(new RecurrenceRule(RecurrenceFrequency.fromValue(modificationRequest.recurrenceRule().frequency()),
                    modificationRequest.recurrenceRule().weekDays(),
                    modificationRequest.recurrenceRule().interval(),
                    modificationRequest.recurrenceRule().endDate()));
        }

        return ScheduleMapper.toScheduleResponseDTO(schedule);
    }

    private ConsultationChannel[] toConsultationChannels(Set<String> consultationChannelStrings) {
        return consultationChannelStrings.stream()
                .map(channel -> ConsultationChannel.valueOf(channel.toUpperCase()))
                .toArray(ConsultationChannel[]::new);
    }

    @Transactional
    public void deleteScheduleById(String id) {
        var consultantId = scheduleRepository.getScheduleForDeletionReturningConsultantId(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule does not exist or has been deleted"));
        if (!consultantId.equals(authUtils.getCurrentUser().userId())) {
            throw new AuthorizationDeniedException("You are not authorized to delete this resource");
        }
        scheduleRepository.deleteScheduleByScheduleId(id);
    }
}
