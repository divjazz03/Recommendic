package com.divjazz.recommendic.appointment.controller.payload;

import com.divjazz.recommendic.appointment.domain.DaysOfWeek;
import com.divjazz.recommendic.appointment.domain.RecurrenceFrequency;
import com.divjazz.recommendic.global.validation.annotation.ValidDate;
import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleRecurrenceRule;
import com.divjazz.recommendic.global.validation.annotation.ValidEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@ScheduleRecurrenceRule
public record RecurrenceRuleRequest(
        @NotNull
        @ValidEnum(enumClass = RecurrenceFrequency.class)
        String frequency,
        Set<@ValidEnum(enumClass = DaysOfWeek.class) String> weekDays,
        int interval,
        @ValidDate
        String endDate
) {
}
