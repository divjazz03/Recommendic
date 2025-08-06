package com.divjazz.recommendic.appointment.service;

import com.divjazz.recommendic.appointment.domain.RecurrenceRule;
import com.divjazz.recommendic.appointment.dto.ScheduleCreationRequest;
import com.divjazz.recommendic.appointment.dto.ScheduleDisplay;
import com.divjazz.recommendic.appointment.dto.ScheduleModificationRequest;
import com.divjazz.recommendic.appointment.dto.ScheduleResponseDTO;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.ScheduleCustomRepository;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.model.Consultant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    public final ScheduleCustomRepository scheduleCustomRepository;
    private final AuthUtils authUtils;

    @Transactional
    public ScheduleResponseDTO createSchedule(ScheduleCreationRequest creationRequest) {

        var consultant =(Consultant) authUtils.getCurrentUser();
        var schedule = Schedule.builder()
                .name(creationRequest.name())
                .consultant(consultant)
                .consultationChannels(toConsultationChannels(creationRequest.channels()))
                .startTime(LocalTime.parse(creationRequest.startTime(), DateTimeFormatter.ISO_TIME))
                .endTime(LocalTime.parse(creationRequest.endTime(), DateTimeFormatter.ISO_TIME))
                .isActive(creationRequest.isActive())
                .isRecurring(creationRequest.isRecurring())
                .zoneOffset(ZoneOffset.of(creationRequest.zoneOffset()))
                .build();
        if (Objects.nonNull(creationRequest.recurrenceRule())) {
            var recurrenceRule = new RecurrenceRule(
                    creationRequest.recurrenceRule().frequency(),
                    creationRequest.recurrenceRule().weekDays(),
                    creationRequest.recurrenceRule().interval(),
                    creationRequest.recurrenceRule().endDate()
            );
            schedule.setRecurrenceRule(recurrenceRule);
        }


        schedule = scheduleRepository.save(schedule);

        return toScheduleResponseDTO(schedule);
    }
    @Transactional(readOnly = true)
    public Set<ScheduleDisplay> getMySchedules() {
        return scheduleCustomRepository.findAllScheduleDisplaysByConsultantId(authUtils.getCurrentUser().getId(), Pageable.ofSize(10));
    }
    public ScheduleResponseDTO getScheduleById(long id) {
        var schedule =  scheduleRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Schedule with id %s not found".formatted(id)));
        return toScheduleResponseDTO(schedule);
    }
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesByConsultantId(String consultantId) {
        return scheduleRepository.findAllByConsultant_UserId(consultantId).toList();
    }
    @Transactional
    public ScheduleResponseDTO modifySchedule(long id, ScheduleModificationRequest modificationRequest) {
        log.info("{}", modificationRequest.toString());
        var schedule = scheduleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Schedule with id %s does not exist or has been deleted".formatted(id)));
        if (!schedule.getConsultant().getUserId().equals(authUtils.getCurrentUser().getUserId())) {
            throw new AuthorizationDeniedException("You are not the owner of this schedule, hence cannot perform this action");
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
            schedule.setEndTime(LocalTime.parse(modificationRequest.startTime()));
        }
        if (Objects.nonNull(modificationRequest.zoneOffset())) {
            schedule.setZoneOffset(ZoneOffset.of(modificationRequest.zoneOffset()));
        }
        schedule.setActive(modificationRequest.isActive());
        boolean scheduleWasRecurring = schedule.isRecurring();
        if (!scheduleWasRecurring && modificationRequest.isRecurring()) {
            schedule.setRecurring(true);
        } else if (!modificationRequest.isRecurring() && scheduleWasRecurring) {
            schedule.setRecurring(false);
        }
        if (schedule.isRecurring()) {
            schedule.setRecurrenceRule(new RecurrenceRule(modificationRequest.recurrenceRule().frequency(),
                    modificationRequest.recurrenceRule().weekDays(),
                    modificationRequest.recurrenceRule().interval(),
                    modificationRequest.recurrenceRule().endDate()));
        } else {
            schedule.setRecurrenceRule(null);
        }

        return toScheduleResponseDTO(schedule);
    }













    private ConsultationChannel[] toConsultationChannels(Set<String> consultationChannelStrings) {
        return consultationChannelStrings.stream()
                .map(channel -> ConsultationChannel.valueOf(channel.toUpperCase()))
                .toArray(ConsultationChannel[]::new);
    }
    private Set<String> fromConsultationChannels(ConsultationChannel[] consultationChannels) {
        return Arrays.stream(consultationChannels)
                .map(consultationChannel -> consultationChannel.toString().toLowerCase())
                .collect(Collectors.toSet());
    }
    private ScheduleDisplay toScheduleDisplay(Schedule schedule) {
        return new ScheduleDisplay(
                schedule.getId(),
                schedule.getName(),
                schedule.getStartTime().format(DateTimeFormatter.ISO_TIME),
                schedule.getEndTime().format(DateTimeFormatter.ISO_TIME),
                schedule.getZoneOffset().toString(),
                fromConsultationChannels(schedule.getConsultationChannels()),
                schedule.isRecurring(),
                schedule.getRecurrenceRule(),
                schedule.isActive(),
                schedule.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                0
        );
    }

    private ScheduleResponseDTO toScheduleResponseDTO(Schedule schedule) {
        return new ScheduleResponseDTO(
                schedule.getId(),
                schedule.getName(),
                schedule.getStartTime().format(DateTimeFormatter.ISO_TIME),
                schedule.getEndTime().format(DateTimeFormatter.ISO_TIME),
                schedule.getZoneOffset().getId(),
                fromConsultationChannels(schedule.getConsultationChannels()),
                schedule.isRecurring(),
                schedule.getRecurrenceRule() != null
                        && schedule.isRecurring() ? schedule.getRecurrenceRule() : null ,
                schedule.isActive()
        );
    }
}
