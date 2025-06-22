package com.divjazz.recommendic.appointment.exception;

public class AppointmentNotFoundException extends RuntimeException {
    public static final String MESSAGE = "Appointment either doesn't exist or is cancelled";

    public AppointmentNotFoundException() {
        super(MESSAGE);
    }
}
