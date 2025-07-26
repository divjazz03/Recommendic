package com.divjazz.recommendic.appointment.dto;

import com.divjazz.recommendic.appointment.validation.appointment.annotation.AppointmentChannel;
import jakarta.validation.constraints.NotNull;

public record AppointmentCreationRequest(
        @NotNull(message = "id is required")
        String consultantId,

        @NotNull(message = "Schedule id is required")
        long scheduleId,
        @AppointmentChannel
        @NotNull(message = "channel is required")
        String channel
) {
}
