package com.divjazz.recommendic.user.exception;

public class ConfirmationTokenExpiredException extends RuntimeException{

    private static final String MESSAGE = "This confirmation token is expired. Request another";

    public ConfirmationTokenExpiredException() {
        super(MESSAGE);
    }
}
