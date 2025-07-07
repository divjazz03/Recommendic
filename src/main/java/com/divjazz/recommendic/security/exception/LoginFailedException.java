package com.divjazz.recommendic.security.exception;

public class LoginFailedException extends RuntimeException {
    public LoginFailedException(String s) {
        super(s);
    }
}
