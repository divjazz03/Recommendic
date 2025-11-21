package com.divjazz.recommendic.consultation.event;

import java.time.LocalDateTime;

public record ConsultationStartedData(
        String consultationId,
        LocalDateTime dateTime
) implements ConsultationEventData {

    public ConsultationStartedData (String consultationId) {
        this(consultationId, LocalDateTime.now());
    }

    @Override
    public ConsultationEvent getEvent() {
        return ConsultationEvent.CONSULTATION_STARTED;
    }
}
