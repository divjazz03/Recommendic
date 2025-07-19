package com.divjazz.recommendic.appointment.service;

import com.divjazz.recommendic.appointment.dto.ScheduleCreationRequest;
import com.divjazz.recommendic.appointment.dto.ScheduleResponseDTO;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.global.converter.ZoneOffsetConverter;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.model.Consultant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final AuthUtils authUtils;

    @Transactional(readOnly = true)
    public Stream<Schedule> getScheduleSlotsForConsultants(String consultantId) {
        return scheduleRepository.findAllByConsultant_UserId(consultantId);
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
        schedule = scheduleRepository.save(schedule);

        return new ScheduleResponseDTO(
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
