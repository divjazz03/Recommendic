package com.divjazz.recommendic.notification.email.service;

import com.divjazz.recommendic.notification.email.util.EmailUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class EmailServiceImpl implements EmailService {

    public static final String PASSWORD_RESET_REQUEST = "Reset Password Request";
    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    private final JavaMailSender sender;
    Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender sender) {
        this.sender = sender;
    }

    @Async("recommendicTaskExecutor")
    @Override
    public void sendNewAccountEmail(String name, String toEmail, String key) {
        try {
            var message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setText(EmailUtils.getEmailMessage(name, host, key));
            //TODO: UNCOMMENT THE EMAIL SENDING FUNCTIONALITY
            //sender.send(message);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    @Async("recommendicTaskExecutor")
    @Override
    public void sendPasswordResetEmail(String name, String toEmail, String key) {
        try {
            var message = new SimpleMailMessage();
            message.setSubject(PASSWORD_RESET_REQUEST);
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setText(EmailUtils.getResetPasswordMessage(name, host, key));
            sender.send(message);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Async("recommendicTaskExecutor")
    @Override
    public void sendNewAdminAccountEmail(String name, String toEmail, String key, String password) {
        try {
            var message = new SimpleMailMessage();
            message.setSubject(fromEmail);
            message.setTo(toEmail);
            message.setText(EmailUtils.getAdminRegistrationEmailMessage(name, toEmail, key, password));
            sender.send(message);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
