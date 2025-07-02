package com.divjazz.recommendic.appointment.exception;

public class AppointmentBookedException extends RuntimeException{

    public AppointmentBookedException() {
    }

    public AppointmentBookedException(String message) {
        super(message);
    }
}
