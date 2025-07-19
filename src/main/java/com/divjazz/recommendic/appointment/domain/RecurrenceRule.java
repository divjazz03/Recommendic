package com.divjazz.recommendic.appointment.domain;

public record RecurrenceRule(
        RecurrenceFrequency frequency,
        DaysOfWeek weekDays,
        int interval,
        String endDate
) {
}
