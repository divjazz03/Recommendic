package com.divjazz.recommendic.notification.email.util;

public class EmailUtils {


    public static String getEmailMessage(String name, String host, String key) {
        return String
                .format("""
                        Hello %s,
                        
                        Your new account has been created. Please click on the link below to verify your account.\s
                        
                        %s
                            
                        The Support Team""", name, getVerificationUrl(host, key));
    }

    public static String getResetPasswordMessage(String name, String host, String key) {
        return String
                .format("""
                        Hello %s,
                        
                        Your reset password token has been created. Please click on the link below to change your password.\s
                        
                        %s
                            
                        The Support Team""", name, getResetPasswordUrl(host, key));
    }

    public static String getAdminRegistrationEmailMessage(String name, String host, String key, String password) {
        return String.format("""
                
                Hello %s,
                
                Your new admin account has been create. Please click the link below to verify your account.\s
                
                %s
                
                Your password is %s
                
                The Support Team""", name, password, getVerificationUrl(host, key));
    }

    private static String getVerificationUrl(String host, String key) {
        return host + "/api/v1/auth/email-token?token=" + key;
    }

    private static String getResetPasswordUrl(String host, String key) {
        return host + "/api/v1/auth/email-token?token=" + key;
    }
}
