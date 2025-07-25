package com.divjazz.recommendic.user.event.listener;

import com.divjazz.recommendic.email.service.EmailService;
import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;


    @EventListener
    public void onUserEvent(UserEvent userEvent){
        switch (userEvent.getEventType()){
            case REGISTRATION -> handleUserRegistration(userEvent);
            case RESET_PASSWORD -> {
                switch (userEvent.getUserType()) {
                    case PATIENT, CONSULTANT -> emailService
                            .sendPasswordResetEmail((String) userEvent.getData().get("firstname"),
                                    (String) userEvent.getData().get("email"),
                                    (String) userEvent.getData().get("key"));
                }
            }

            case ADMIN_REGISTRATION -> emailService
                    .sendNewAdminAccountEmail((String) userEvent.getData().get("firstname"),
                            (String) userEvent.getData().get("email"),
                            (String) userEvent.getData().get("key"),
                            (String) userEvent.getData().get("password"));

        }
    }

    void handleUserRegistration(UserEvent userEvent) {
        switch (userEvent.getUserType()) {
            case PATIENT, CONSULTANT -> emailService
                    .sendNewAccountEmail((String) userEvent.getData().get("firstname"),
                            (String) userEvent.getData().get("email"),
                            (String) userEvent.getData().get("key"));
        }
    }
}
