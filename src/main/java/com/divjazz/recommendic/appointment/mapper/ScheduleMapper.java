package com.divjazz.recommendic.appointment.mapper;

import com.divjazz.recommendic.appointment.dto.ScheduleResponseDTO;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ScheduleMapper {

    public static ScheduleResponseDTO toScheduleResponseDTO(Schedule schedule) {
        return new ScheduleResponseDTO(
                schedule.getScheduleId(),
                schedule.getName(),
                schedule.getStartTime().format(DateTimeFormatter.ISO_TIME),
                schedule.getEndTime().format(DateTimeFormatter.ISO_TIME),
                schedule.getZoneOffset().getId(),
                fromConsultationChannels(schedule.getConsultationChannels()),
                schedule.getRecurrenceRule() != null
                        ? schedule.getRecurrenceRule() : null,
                schedule.isActive()
        );
    }

    private static Set<String> fromConsultationChannels(ConsultationChannel[] consultationChannels) {
        return Arrays.stream(consultationChannels)
                .map(consultationChannel -> consultationChannel.toString().toLowerCase())
                .collect(Collectors.toSet());
    }
}
