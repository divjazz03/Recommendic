package com.divjazz.recommendic.appointment.dto;

import com.divjazz.recommendic.appointment.enums.AppointmentHistory;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
public record AppointmentDTO(
        String patientId,
        String consultantId,
        String patientFullName,
        String consultantFullName,
        AppointmentStatus status,
        String startDateTime,
        String endDateTime,
        ConsultationChannel consultationChannel,
        AppointmentHistory appointmentHistory,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,

        String reason,
        String note,

        Instant createdAt,
        Instant updatedAt
) {
}
