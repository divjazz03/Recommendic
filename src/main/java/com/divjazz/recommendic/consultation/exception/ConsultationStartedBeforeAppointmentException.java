package com.divjazz.recommendic.consultation.exception;

public class ConsultationStartedBeforeAppointmentException extends RuntimeException{
    public static final String MESSAGE = "Consultation cannot be started less than 15 minutes before the appointed time";

    public ConsultationStartedBeforeAppointmentException() {
        super(MESSAGE);
    }
}
