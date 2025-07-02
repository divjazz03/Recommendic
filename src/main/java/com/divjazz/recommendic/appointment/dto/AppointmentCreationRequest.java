package com.divjazz.recommendic.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record AppointmentCreationRequest(
        @NotNull(message = "id is required")
        String consultantId,

        long scheduleId,

        @NotNull(message = "startTime is required")
        String startTime,

        @NotNull(message = "startTime is required")
        String endTime,
        @NotNull(message = "appointment date is required")
        @Future
        String appointmentDate,
        @NotNull(message = "channel is required")
        String channel
) {
}
