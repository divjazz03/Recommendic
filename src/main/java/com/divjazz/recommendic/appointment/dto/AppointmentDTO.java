package com.divjazz.recommendic.appointment.dto;

public record AppointmentDTO(
        String patientId,
        String patientFullName,
        String consultantId,
        String consultantFullName,
        String status,
        String startDateTime,
        String endDateTime,
        String channel
) {
}
