package com.divjazz.recommendic.appointment.dto;

import com.divjazz.recommendic.appointment.domain.RecurrenceRule;

import java.util.Set;

public record ScheduleResponseDTO(
        String id,
        String name,
        String startTime,
        String endTime,
        String offset,
        Set<String> channels,
        RecurrenceRule recurrenceRule,
        boolean isActive
) {
}
