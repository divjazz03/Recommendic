package com.divjazz.recommendic.email.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.divjazz.recommendic.email.util.EmailUtils.*;


@Service
public class EmailServiceImpl implements EmailService{

    public static final String PASSWORD_RESET_REQUEST = "Reset Password Request";
    Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    private final JavaMailSender sender;
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender sender) {
        this.sender = sender;
    }

    @Async
    @Override
    public void sendNewAccountEmail(String name, String toEmail, String key) {
        try {
            var message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setText(getEmailMessage(name, host, key));
            sender.send(message);
        } catch (Exception e){
            logger.error(e.getMessage());
        }

    }

    @Async
    @Override
    public void sendPasswordResetEmail(String name, String toEmail, String key) {
        try {
            var message = new SimpleMailMessage();
            message.setSubject(PASSWORD_RESET_REQUEST);
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setText(getResetPasswordMessage(name, host, key));
            sender.send(message);
        } catch (Exception e){
            logger.error(e.getMessage());
        }
    }
    @Async
    @Override
    public void sendNewAdminAccountEmail(String name, String toEmail, String key, String password) {
        try {
            var message = new SimpleMailMessage();
            message.setSubject(fromEmail);
            message.setTo(toEmail);
            message.setText(getAdminRegistrationEmailMessage(name, toEmail, key, password));
            sender.send(message);
        } catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
