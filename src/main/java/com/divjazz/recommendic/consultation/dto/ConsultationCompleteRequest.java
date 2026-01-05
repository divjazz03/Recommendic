package com.divjazz.recommendic.consultation.dto;

import com.divjazz.recommendic.global.validation.annotation.ValidDate;
import com.divjazz.recommendic.consultation.validation.annotation.ConsultationCompleteValidationRequest;
import jakarta.validation.constraints.NotNull;

@ConsultationCompleteValidationRequest
public record ConsultationCompleteRequest(
        @NotNull
        String consultationId,
        @NotNull
        String summary,
        boolean shouldReschedule,
        String scheduleId,
        @ValidDate
        String date,
        String reason,
        String patientStatus
        ) {
}
