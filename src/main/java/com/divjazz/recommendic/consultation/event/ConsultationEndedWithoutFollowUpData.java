package com.divjazz.recommendic.consultation.event;

import java.time.LocalDateTime;

public record ConsultationEndedWithoutFollowUpData(
        String consultationId,
        String patientId,
        String consultantId,
        LocalDateTime dateTime
) implements ConsultationEventData{

    public ConsultationEndedWithoutFollowUpData(String consultationId, String patientId, String consultantId) {
        this(consultationId, patientId,consultantId, LocalDateTime.now());
    }

    @Override
    public ConsultationEvent getEvent() {
        return ConsultationEvent.CONSULTATION_ENDED_WITHOUT_FOLLOWUP;
    }
}
