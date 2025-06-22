package com.divjazz.recommendic.consultation.exception;

public class ConsultationNotFoundException extends RuntimeException{
    public static final String MESSAGE = "Consultation with id %s not found";

    public ConsultationNotFoundException(Long id) {
        super(MESSAGE.formatted(String.valueOf(id)));
    }
}
