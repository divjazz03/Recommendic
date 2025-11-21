package com.divjazz.recommendic.consultation.dto;

import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleDate;
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
        @ScheduleDate
        String date,
        String reason
        ) {
}
