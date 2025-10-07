package com.divjazz.recommendic.notification.email.service;

public interface EmailService {
    void sendNewAccountEmail(String name, String to, String key);
    void sendPasswordResetEmail(String name, String to, String key);
    void sendNewAdminAccountEmail(String name, String to, String key, String password);
}
