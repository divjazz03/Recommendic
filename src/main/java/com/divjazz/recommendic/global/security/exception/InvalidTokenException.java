package com.divjazz.recommendic.global.security.exception;

public class InvalidTokenException extends RuntimeException {
    private static final String INVALID_TOKEN_EXCEPTION_MESSAGE = "The provided token is invalid";

    public InvalidTokenException() {
        super(INVALID_TOKEN_EXCEPTION_MESSAGE);
    }
}
