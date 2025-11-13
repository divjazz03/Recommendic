package com.divjazz.recommendic.appointment.controller.payload;

import com.divjazz.recommendic.appointment.domain.AppointmentPriority;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record ConsultantAppointmentResponse(
        String id,
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
) implements AppointmentResponse{
}
