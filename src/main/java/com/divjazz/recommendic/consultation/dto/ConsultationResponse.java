package com.divjazz.recommendic.consultation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@JsonInclude(value = NON_DEFAULT)
public record ConsultationResponse(
        String summary,
        String startTime,
        String patientName,
        String consultantName,
        String consultationId,
        String status,
        String channel,
        PatientData patientData
) {
}
