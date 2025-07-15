package com.divjazz.recommendic.appointment.mapper;

import com.divjazz.recommendic.appointment.dto.AppointmentDTO;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.repository.projection.AppointmentProjection;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;

import java.time.format.DateTimeFormatter;

public class AppointmentMapper {

    public static AppointmentDTO appointmentProjectionToDTO (AppointmentProjection appointment) {
        var endDateIsNotNull = appointment.appointment().getEndDateAndTime() != null;
        return new AppointmentDTO(
                appointment.appointment().getPatient().getUserId(),
                appointment.patientProfile().getUserName().getFullName(),
                appointment.appointment().getPatient().getUserId(),
                appointment.consultantProfile().getUserName().getFullName(),
                appointment.appointment().getStatus().toString(),
                appointment.appointment().getStartDateAndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                endDateIsNotNull? appointment.appointment().getEndDateAndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null,
                appointment.appointment().getConsultationChannel().toString()
        );
    }
}
