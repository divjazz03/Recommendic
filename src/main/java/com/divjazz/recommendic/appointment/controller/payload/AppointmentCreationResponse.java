package com.divjazz.recommendic.appointment.controller.payload;

import com.divjazz.recommendic.appointment.enums.AppointmentHistory;

public record AppointmentCreationResponse(
        String appointmentId,
        String patientFullName,
        String patientId,
        String consultantFullName,
        String consultantId,
        String status,
        String startTime,
        String endTime,
        String channel,
        AppointmentHistory history
) {
}
