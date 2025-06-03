package com.divjazz.recommendic.user.exception;

public class ConfirmationTokenNotFoundException extends RuntimeException{

    public static final String MESSAGE = "Confirmation token not found please sign up";

    public ConfirmationTokenNotFoundException() {
        super(MESSAGE);
    }
}
