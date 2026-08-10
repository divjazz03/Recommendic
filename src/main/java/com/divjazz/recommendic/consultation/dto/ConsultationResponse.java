package com.divjazz.recommendic.consultation.dto;

import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@JsonInclude(value = NON_DEFAULT)
public record ConsultationResponse(
        String summary,
        String startTime,
        String patientName,
        String patientId,
        String consultantName,
        String consultantId,
        String consultationId,
        ConsultationStatus status,
        ConsultationChannel channel,
        PatientData patientData,
        ConsultantData consultantData
) {
}
