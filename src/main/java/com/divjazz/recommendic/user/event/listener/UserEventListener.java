package com.divjazz.recommendic.user.event.listener;

import com.divjazz.recommendic.email.service.EmailService;
import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;
    private final RecommendationService recommendationService;


    @EventListener
    public void onUserEvent(UserEvent userEvent){
        switch (userEvent.getEventType()){
            case REGISTRATION -> handleUserRegistration(userEvent);
            case RESET_PASSWORD -> emailService
                    .sendPasswordResetEmail(userEvent.getUser().getUserNameObject().getFirstName(),
                            userEvent.getUser().getEmail(),
                            (String) userEvent.getData().get("key"));
            case ADMIN_REGISTRATION -> emailService
                    .sendNewAdminAccountEmail(userEvent.getUser().getUserNameObject().getFirstName(),
                            userEvent.getUser().getEmail(),
                            (String) userEvent.getData().get("key"),
                            (String) userEvent.getData().get("password"));

        }
    }

    void handleUserRegistration(UserEvent userEvent) {
        emailService
                .sendNewAccountEmail(userEvent.getUser().getUserNameObject().getFirstName(),
                        userEvent.getUser().getEmail(),
                        (String) userEvent.getData().get("key"));
        if (userEvent.getUser().getUserType() == UserType.PATIENT && userEvent.getUser() instanceof Patient patient) {
            recommendationService.createArticleRecommendationsForPatient(patient);
        }
    }
}
