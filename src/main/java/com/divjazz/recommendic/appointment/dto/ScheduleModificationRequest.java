package com.divjazz.recommendic.appointment.dto;

import com.divjazz.recommendic.appointment.validation.schedule.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@ScheduleRecurrenceModification
public record ScheduleModificationRequest(
        String name,
        @ScheduleTime
        String startTime,
        @ScheduleTime
        String endTime,
        @ScheduleZone
        String zoneOffset,
        @ScheduleChannel
        Set<String> channels,
        boolean isRecurring,
        @ScheduleRecurrenceRule
        @Valid
        RecurrenceRuleRequest recurrenceRule,
        boolean isActive
) {
}
