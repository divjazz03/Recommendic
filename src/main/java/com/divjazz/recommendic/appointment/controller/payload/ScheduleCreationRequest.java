package com.divjazz.recommendic.appointment.controller.payload;

import com.divjazz.recommendic.appointment.validation.schedule.annotation.*;
import com.divjazz.recommendic.global.validation.annotation.ValidTime;
import com.divjazz.recommendic.global.validation.annotation.ValidZone;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record ScheduleCreationRequest(
        @NotBlank(message = "name is required")
        String name,
        @NotBlank(message = "Start time is required")
        @ValidTime
        String startTime,
        @NotBlank(message = "End time is required")
        @ValidTime
        String endTime,
        @NotBlank(message = "Zone offset is required")
        @ValidZone
        String zoneOffset,
        @NotNull(message = "Channels is required")
        @ScheduleChannel
        @Size(min = 1, message = "Should contain at least one channel")
        Set<String> channels,
        @Valid @NotNull RecurrenceRuleRequest recurrenceRule,
        boolean isActive
) {
}
