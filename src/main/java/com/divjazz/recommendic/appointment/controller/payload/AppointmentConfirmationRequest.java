package com.divjazz.recommendic.appointment.controller.payload;

public record AppointmentConfirmationRequest(
        String appointmentId,
        String note
) {
}
