package com.divjazz.recommendic.appointment.repository.projection;

import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.model.userAttributes.UserName;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

public interface AppointmentProjection {
    String getPatientId();
    UserName getPatientFullName();
    String getConsultantId();
    UserName getConsultantFullName();
    AppointmentStatus getStatus();
    LocalDate getEndDate();
    LocalDate getStartDate();
    LocalTime getStartTime();
    LocalTime getEndTime();
    ConsultationChannel getChannel();
    ZoneOffset getOffset();
}
