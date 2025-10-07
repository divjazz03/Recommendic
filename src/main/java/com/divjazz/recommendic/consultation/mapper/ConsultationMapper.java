package com.divjazz.recommendic.consultation.mapper;

import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.repository.ConsultationProjection;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;

public class ConsultationMapper {

    public static ConsultationResponse consultationToConsultationResponse(ConsultationProjection consultationProjection) {
        var patientName = consultationProjection.patientProfile().getUserName();
        var consultantName = consultationProjection.consultantProfile().getUserName();
        return new ConsultationResponse(
                consultationProjection.summary(),
                consultationProjection.startedAt().toString(),
                patientName.getFullName(),
                consultantName.getFullName(),
                consultationProjection.id(),
                consultationProjection.consultationStatus().toString(),
                consultationProjection.appointment().getConsultationChannel().toString(),
                null
        );
    }
    public static ConsultationResponse consultationToConsultationResponse(Consultation consultation) {
        return new ConsultationResponse(
                consultation.getSummary(),
                consultation.getStartedAt().toString(),
                consultation.getAppointment().getPatient().getPatientProfile().getUserName().getFullName(),
                consultation.getAppointment().getConsultant().getProfile().getUserName().getFullName(),
                consultation.getConsultationId(),
                consultation.getConsultationStatus().toString(),
                consultation.getAppointment().getConsultationChannel().toString(),
                null
        );
    }
}
