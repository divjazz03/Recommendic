package com.divjazz.recommendic.appointment.dto;


import java.util.List;

public record ScheduleWithAppointmentDetail(
        ScheduleResponseDTO schedule,
        List<String> appointmentDateAndTimes
) {
}
