package com.divjazz.recommendic.appointment.mapper;

import com.divjazz.recommendic.appointment.dto.AppointmentDTO;
import com.divjazz.recommendic.appointment.model.Appointment;

import java.time.format.DateTimeFormatter;

public class AppointmentMapper {

    public static AppointmentDTO appointmentToDTO (Appointment appointment) {
        var endDateIsNotNull = appointment.getEndDateAndTime() != null;
        return new AppointmentDTO(
                appointment.getPatient().getUserId(),
                appointment.getPatient().getPatientProfile().getUserName().getFullName(),
                appointment.getPatient().getUserId(),
                appointment.getConsultant().getProfile().getUserName().getFullName(),
                appointment.getStatus().toString(),
                appointment.getStartDateAndTime().format(DateTimeFormatter.ISO_DATE_TIME),
                endDateIsNotNull? appointment.getEndDateAndTime().format(DateTimeFormatter.ISO_DATE_TIME) : null,
                appointment.getConsultationChannel().toString()
        );
    }
}
