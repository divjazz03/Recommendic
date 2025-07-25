package com.divjazz.recommendic.appointment.domain;

import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleDate;
import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleDayOfWeeks;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;


public record RecurrenceRule(
        RecurrenceFrequency frequency,
        Set<String> weekDays,
        int interval,
        String endDate
) {
}
