package com.divjazz.recommendic.appointment.controller.payload;

import com.divjazz.recommendic.appointment.validation.appointment.annotation.AppointmentChannel;
import jakarta.validation.constraints.NotNull;

public record AppointmentCreationRequest(
        @NotNull(message = "id is required")
        String consultantId,

        @NotNull(message = "Schedule id is required")
        String scheduleId,
        @AppointmentChannel
        @NotNull(message = "channel is required")
        String channel,
        @NotNull(message = "Date cannot be null")
        String date,
        @NotNull(message = "Reason is required")
        String reason
) {
}
