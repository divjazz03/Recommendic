package com.divjazz.recommendic.consultation.mapper;

import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.model.Consultation;

public class ConsultationMapper {

    public static ConsultationResponse consultationToConsultationResponse(Consultation consultation) {
        var patientName = consultation.getAppointment().getPatient().getUserNameObject();
        var consultantName = consultation.getAppointment().getConsultant().getUserNameObject();
        return new ConsultationResponse(
                consultation.getSummary(),
                consultation.getStartedAt().toString(),
                patientName.getFullName(),
                consultantName.getFullName(),
                consultation.getId(),
                consultation.getConsultationStatus().toString(),
                consultation.getAppointment().getConsultationChannel().toString()
        );
    }
}
