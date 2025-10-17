package com.divjazz.recommendic.appointment.controller.payload;

import com.divjazz.recommendic.appointment.domain.RecurrenceFrequency;
import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleDate;
import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleDayOfWeeks;
import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleRecurrenceRule;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@ScheduleRecurrenceRule
public record RecurrenceRuleRequest(
        @NotNull
        RecurrenceFrequency frequency,
        @ScheduleDayOfWeeks
        Set<String> weekDays,
        int interval,
        @ScheduleDate
        String endDate
) {
}
