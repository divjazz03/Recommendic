package com.divjazz.recommendic.appointment.dto;

import com.divjazz.recommendic.appointment.domain.RecurrenceRule;

import java.util.Set;

public record ScheduleCreationRequest(
        String name,
        String startTime,
        String endTime,
        String zoneOffset,
        Set<String> channels,
        boolean isRecurring,
        RecurrenceRule recurrenceRule,
        boolean isActive
) {
}
