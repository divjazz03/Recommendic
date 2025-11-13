package com.divjazz.recommendic.appointment.controller.payload;

import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;

public record PatientAppointmentResponse (
        String id,
        String consultantId,
        String doctorName,
        String specialty,
        String date,
        String time,
        String duration,
        ConsultationChannel type,
        String location,
        String phone,
        AppointmentStatus status,
        String notes,
        String preparation
) implements AppointmentResponse {
}
