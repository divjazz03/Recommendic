package com.divjazz.recommendic.appointment.exception;

public class ScheduleNotAvailableException extends RuntimeException{
    public static final String MESSAGE = "Schedule with start_time %s and end_time %s not available";

    public ScheduleNotAvailableException(String startTime, String endTime) {
        super(MESSAGE.formatted(startTime,endTime));
    }
}
