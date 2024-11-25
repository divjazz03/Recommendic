package com.divjazz.recommendic.user.exception;

public class NoSuchMedicalCategory extends RuntimeException{

    private static final String MESSAGE = "THE MEDICAL CATEGORY IS INVALID";
    public NoSuchMedicalCategory() {
        super(MESSAGE);
    }
}
