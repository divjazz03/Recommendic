package com.divjazz.recommendic.appointment.mapper;

import com.divjazz.recommendic.appointment.dto.AppointmentDTO;
import com.divjazz.recommendic.appointment.model.Appointment;

import java.time.format.DateTimeFormatter;

public class AppointmentMapper {

    public static AppointmentDTO appointmentToDTO (Appointment appointment) {
        return new AppointmentDTO(
                appointment.getPatient().getUserId(),
                appointment.getPatient().getUserNameObject().getFullName(),
                appointment.getConsultant().getUserId(),
                appointment.getConsultant().getUserNameObject().getFullName(),
                appointment.getStatus().toString(),
                appointment.getScheduleSlot().getStartTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                appointment.getScheduleSlot().getEndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                appointment.getScheduleSlot().getConsultationChannel().toString()
        );
    }
}
