package com.divjazz.recommendic.appointment.controller.payload;

public record AppointmentCreationResponse(
        String appointmentId,
        String patientFullName,
        String patientId,
        String consultantFullName,
        String consultantId,
        String status,
        String startTime,
        String endTime,
        String channel
) {
}
