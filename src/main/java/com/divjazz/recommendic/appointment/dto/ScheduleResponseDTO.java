package com.divjazz.recommendic.appointment.dto;

import com.divjazz.recommendic.appointment.domain.RecurrenceRule;

import java.util.Set;

public record ScheduleResponseDTO(
        String name,
        String startTime,
        String endTime,
        String offset,
        Set<String> channels,
        boolean isRecurring,
        RecurrenceRule recurrenceRule,
        boolean isActive
) {
}
