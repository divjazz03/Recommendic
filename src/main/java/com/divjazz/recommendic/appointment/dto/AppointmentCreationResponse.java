package com.divjazz.recommendic.appointment.dto;

public record AppointmentCreationResponse(
        String patientFullName,
        String consultantFullName,
        String status,
        String startTime,
        String endTime,
        String channel
) {
}
