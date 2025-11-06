package com.divjazz.recommendic.appointment.controller.payload;

import com.divjazz.recommendic.appointment.domain.RecurrenceRule;

import java.util.Set;

public record ScheduleDisplay(
        String id,
        String name,
        String startTime,
        String endTime,
        String offset,
        Set<String> channels,
        RecurrenceRule recurrenceRule,
        boolean isActive,
        String createdAt,
        int upcomingSessions
) {
}
