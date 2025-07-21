package com.divjazz.recommendic.appointment.domain;

import com.divjazz.recommendic.appointment.validation.annotation.ScheduleDate;
import com.divjazz.recommendic.appointment.validation.annotation.ScheduleDayOfWeeks;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record RecurrenceRule(
        RecurrenceFrequency frequency,
        @ScheduleDayOfWeeks
        Set<String> weekDays,
        int interval,
        @ScheduleDate
        String endDate
) {
}
