package com.divjazz.recommendic.appointment.dto;

import com.divjazz.recommendic.appointment.domain.RecurrenceFrequency;
import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleDate;
import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleDayOfWeeks;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record RecurrenceRuleRequest(
        RecurrenceFrequency frequency,
        @ScheduleDayOfWeeks
        Set<String> weekDays,
        int interval,
        @ScheduleDate
        String endDate
) {
}
