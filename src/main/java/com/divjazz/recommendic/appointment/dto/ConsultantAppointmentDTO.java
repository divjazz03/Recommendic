package com.divjazz.recommendic.appointment.dto;

import com.divjazz.recommendic.appointment.domain.AppointmentPriority;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;

public record ConsultantAppointmentDTO(
        String id,
        String patientId,
        String patientName,
        String patientAge,
        String patientPhone,
        String patientEmail,
        String date,
        String time,
        String duration,
        ConsultationChannel type,
        String location,
        String reason,
        String symptoms,
        String medicalHistory,
        String requestedDate,
        AppointmentPriority priority,
        String notes,
        String cancellationReason,
        AppointmentStatus status
) {
}
