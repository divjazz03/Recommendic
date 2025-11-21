package com.divjazz.recommendic.consultation.event;


public sealed interface ConsultationEventData permits ConsultationEndedWithRescheduleData,
        ConsultationEndedWithoutFollowUpData,
        ConsultationStartedData,
        ConsultationStartedEventData {

    ConsultationEvent getEvent();
}
