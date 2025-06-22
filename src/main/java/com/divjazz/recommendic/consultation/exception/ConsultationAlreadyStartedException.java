package com.divjazz.recommendic.consultation.exception;

public class ConsultationAlreadyStartedException extends RuntimeException{

    public static final String MESSAGE = "Consultation already exists";
    public ConsultationAlreadyStartedException() {
        super(MESSAGE);
    }

    public ConsultationAlreadyStartedException(String message) {
        super(message);
    }
}
