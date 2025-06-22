package com.divjazz.recommendic.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record AppointmentCreationRequest(
        @NotNull(message = "id is required")
        String consultantId,
        @Future
        @NotNull(message = "startTime is required")
        String startTime,
        @Future
        @NotNull(message = "startTime is required")
        String endTime,
        @NotNull(message = "channel is required")
        String channel
) {
}
