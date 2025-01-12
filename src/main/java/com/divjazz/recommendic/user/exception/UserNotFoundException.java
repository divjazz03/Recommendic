package com.divjazz.recommendic.user.exception;

public class UserNotFoundException extends RuntimeException {

    public static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "No such user was found";

    public UserNotFoundException() {
        super(USER_NOT_FOUND_EXCEPTION_MESSAGE);
    }
}
