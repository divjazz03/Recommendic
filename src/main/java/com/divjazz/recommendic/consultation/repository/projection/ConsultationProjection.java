package com.divjazz.recommendic.consultation.repository.projection;

import com.divjazz.recommendic.appointment.enums.AppointmentHistory;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.user.model.userAttributes.UserName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

public interface ConsultationProjection {
    String getConsultationId();
    LocalDate getPatientDateOfBirth();
    String getPatientId();
    UserName getPatientFullName();
    String getConsultantId();
    UserName getConsultantFullName();
    ConsultationStatus getStatus();
    LocalDate getEndDate();
    LocalDate getStartDate();
    LocalTime getStartTime();
    LocalTime getEndTime();
    ConsultationChannel getChannel();
    ZoneOffset getOffset();
    AppointmentHistory getAppointmentHistory();
    String getReason();
    LocalDateTime getStartedAt();
    LocalDateTime getEndedAt();

}
