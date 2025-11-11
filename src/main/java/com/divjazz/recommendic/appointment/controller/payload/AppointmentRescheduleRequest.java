package com.divjazz.recommendic.appointment.controller.payload;

import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AppointmentRescheduleRequest(
        @NotNull(message = "appointmentId is required")
        String appointmentId,
        @NotNull(message = "reason is required")
        @NotBlank(message = "reason cannot be empty or blank")
        @Size(max = 1024, min = 16, message = "Reason should have no less than 10 characters and no more than 1024 characters")
        String reason,
        @NotNull(message = "newDate is required")
        @ScheduleDate
        String newDate
) {
}
