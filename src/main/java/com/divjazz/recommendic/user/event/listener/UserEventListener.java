package com.divjazz.recommendic.user.event.listener;

import com.divjazz.recommendic.email.service.EmailService;
import com.divjazz.recommendic.user.event.UserEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private final EmailService emailService;

    public UserEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void onUserEvent(UserEvent userEvent){
        switch (userEvent.getEventType()){
            case REGISTRATION -> emailService
                    .sendNewAccountEmail(userEvent.getUser().getUserNameObject().getFirstName(),
                            userEvent.getUser().getEmail(),
                            (String) userEvent.getData().get("key"));
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
}
