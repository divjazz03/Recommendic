package com.divjazz.recommendic.consultation.dto;

import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import lombok.Builder;

@Builder
public record ConsultationResponseMinimal(
        String summary,
        String startTime,
        String date,
        String reason,
        String consultationId,
        String appointmentId,
        ConsultationStatus status,
        ConsultationChannel channel,
        PatientDataMinimal patientData,
        ConsultantData consultantData
) {
}
