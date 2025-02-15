package com.divjazz.recommendic.user.exception;

public class UserAlreadyExistsException extends RuntimeException {

    private static final String MESSAGE = "User with email %s already exists";

    public UserAlreadyExistsException(String email) {
        super(String.format(MESSAGE, email));
    }
}