package com.divjazz.recommendic.notification.event.listener;

import com.divjazz.recommendic.consultation.event.ConsultationEndedWithoutFollowUpData;
import com.divjazz.recommendic.consultation.event.ConsultationEventData;
import com.divjazz.recommendic.consultation.event.ConsultationStartedEventData;
import com.divjazz.recommendic.notification.app.dto.NotificationDTO;
import com.divjazz.recommendic.notification.app.enums.NotificationCategory;
import com.divjazz.recommendic.notification.app.service.AppNotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ConsultationEventListenerForNotification {
    public static final String CONSULTATION_STARTED_HEADER = "Consultation Session Started";
    public static final String CONSULTATION_STARTED_SUMMARY = "A consultation has started click here to go to the ongoing consultation";
    public static final String CONSULTATION_ENDED_WITHOUT_FOLLOW_UP_HEADER = "Consultation Session Completed";
    public static final String CONSULTATION_ENDED_WITHOUT_FOLLOW_UP_SUMMARY = "Consultation has ended";
    private final AppNotificationService appNotificationService;

    public ConsultationEventListenerForNotification(AppNotificationService appNotificationService) {
        this.appNotificationService = appNotificationService;
    }

    @EventListener
    public void onConsultationEvent(ConsultationEventData eventData) {
        switch (eventData.getEvent()) {
            case CONSULTATION_STARTED -> {
                var data = (ConsultationStartedEventData) eventData;
                appNotificationService.createNotification(
                        new NotificationDTO(
                                CONSULTATION_STARTED_HEADER,
                                CONSULTATION_STARTED_SUMMARY,
                                data.consultantId(),
                                data.consultationId(),
                                false,
                                NotificationCategory.CONSULTATION,
                                null

                        )
                );
                appNotificationService.createNotification(
                        new NotificationDTO(
                                CONSULTATION_STARTED_HEADER,
                                CONSULTATION_STARTED_SUMMARY,
                                data.patientId(),
                                data.consultationId(),
                                false,
                                NotificationCategory.CONSULTATION,
                                null

                        )
                );
            }
            case CONSULTATION_ENDED_WITHOUT_FOLLOWUP -> {
                var data = (ConsultationEndedWithoutFollowUpData) eventData;

                appNotificationService.createNotification(
                        new NotificationDTO(
                                CONSULTATION_ENDED_WITHOUT_FOLLOW_UP_HEADER,
                                CONSULTATION_ENDED_WITHOUT_FOLLOW_UP_SUMMARY,
                                data.consultantId(),
                                data.consultationId(),
                                false,
                                NotificationCategory.CONSULTATION,
                                null

                        )
                );
                appNotificationService.createNotification(
                        new NotificationDTO(
                                CONSULTATION_ENDED_WITHOUT_FOLLOW_UP_HEADER,
                                CONSULTATION_ENDED_WITHOUT_FOLLOW_UP_SUMMARY,
                                data.patientId(),
                                data.consultationId(),
                                false,
                                NotificationCategory.CONSULTATION,
                                null

                        )
                );
            }
        }
    }
}
