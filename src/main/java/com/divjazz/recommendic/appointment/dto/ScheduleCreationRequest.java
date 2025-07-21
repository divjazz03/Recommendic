package com.divjazz.recommendic.appointment.dto;

import com.divjazz.recommendic.appointment.domain.RecurrenceRule;
import com.divjazz.recommendic.appointment.validation.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@ScheduleRecurrence
public record ScheduleCreationRequest(
        @NotNull(message = "name is required")
        String name,
        @NotNull(message = "Start time is required")
        @ScheduleTime
        String startTime,
        @NotNull(message = "End time is required")
        @ScheduleTime
        String endTime,
        @NotNull(message = "Zone offset is required")
        @ScheduleZone
        String zoneOffset,
        @NotNull(message = "Channels is required")
        @ScheduleChannel
        @Size(min = 1, message = "Should contain at least one channel")
        Set<String> channels,
        boolean isRecurring,
        @ScheduleRecurrenceRule
        RecurrenceRule recurrenceRule,
        boolean isActive
) {
}
