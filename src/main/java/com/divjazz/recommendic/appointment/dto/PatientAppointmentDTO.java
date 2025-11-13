package com.divjazz.recommendic.appointment.dto;

import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;

public record PatientAppointmentDTO(
        String id,
        String consultantId,
        String doctorName,
        String specialty,
        String consultantEmail,
        String date,
        String time,
        String duration,
        ConsultationChannel type,
        String location,
        String phone,
        AppointmentStatus status,
        String notes,
        String preparation
) {
}
