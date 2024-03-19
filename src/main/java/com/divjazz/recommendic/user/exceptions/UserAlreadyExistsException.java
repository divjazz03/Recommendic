package com.divjazz.recommendic.user.exceptions;

import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.Email;

public class UserAlreadyExistsException extends RuntimeException{

    private static final String MESSAGE = "User with email %s already exists";
    public UserAlreadyExistsException(String email) {
        super(String.format(MESSAGE, email));
    }
}