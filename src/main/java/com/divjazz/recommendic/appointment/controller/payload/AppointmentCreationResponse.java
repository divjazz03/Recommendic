package com.divjazz.recommendic.appointment.controller.payload;

public record AppointmentCreationResponse(
        String patientFullName,
        String consultantFullName,
        String status,
        String startTime,
        String endTime,
        String channel
) {
}
