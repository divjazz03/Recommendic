package com.divjazz.recommendic.appointment.domain;

import java.util.Set;


public record RecurrenceRule(
        RecurrenceFrequency frequency,
        Set<String> weekDays,
        int interval,
        String endDate
) {
}
