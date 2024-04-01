package com.divjazz.recommendic.user.exceptions;

public class NoSuchMedicalCategory extends RuntimeException{

    private static final String MESSAGE = "THE MEDICAL CATEGORY IS INVALID";
    public NoSuchMedicalCategory() {
        super(MESSAGE);
    }
}
