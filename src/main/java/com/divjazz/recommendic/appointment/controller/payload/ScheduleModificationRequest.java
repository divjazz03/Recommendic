package com.divjazz.recommendic.appointment.controller.payload;

import com.divjazz.recommendic.appointment.validation.schedule.annotation.*;
import com.divjazz.recommendic.global.validation.annotation.ValidTime;
import com.divjazz.recommendic.global.validation.annotation.ValidZone;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@ScheduleRecurrenceModification
public record ScheduleModificationRequest(
        String name,
        @ValidTime
        String startTime,
        @ValidTime
        String endTime,
        @ValidZone
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
