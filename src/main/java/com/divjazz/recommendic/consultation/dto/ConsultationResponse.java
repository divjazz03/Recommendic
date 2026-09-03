package com.divjazz.recommendic.consultation.dto;

import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@JsonInclude(value = NON_DEFAULT)
@Builder
public record ConsultationResponse(
        String reason,
        String startTime,
        String endTime,
        String startedAt,
        String endedAt,
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
