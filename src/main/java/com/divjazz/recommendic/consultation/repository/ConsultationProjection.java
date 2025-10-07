package com.divjazz.recommendic.consultation.repository;

import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;

import java.time.LocalDateTime;

public record ConsultationProjection(
        String id,
        Appointment appointment,
        PatientProfile patientProfile,
        ConsultantProfile consultantProfile,
        String summary,
        ConsultationStatus consultationStatus,
        ConsultationChannel channel,
        LocalDateTime endedAt,
        LocalDateTime startedAt
) {
}
