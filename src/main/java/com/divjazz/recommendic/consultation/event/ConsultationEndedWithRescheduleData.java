package com.divjazz.recommendic.consultation.event;

import com.divjazz.recommendic.consultation.enums.ConsultationChannel;

import java.time.LocalDateTime;

public record ConsultationEndedWithRescheduleData(
        String consultationId,
        String scheduleId,
        ConsultationChannel channel,
        String rescheduleDate,
        LocalDateTime dateTime,
        String consultantId,
        String reason
) implements ConsultationEventData {

    public ConsultationEndedWithRescheduleData (String consultationId, String scheduleId, String rescheduleDate, ConsultationChannel channel, String consultantId, String reason) {
        this(consultationId,scheduleId,channel,rescheduleDate,LocalDateTime.now(), consultantId, reason);
    }
    @Override
    public ConsultationEvent getEvent() {
        return ConsultationEvent.CONSULTATION_ENDED_WITH_FOLLOWUP;
    }
}
