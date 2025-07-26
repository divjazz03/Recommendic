package com.divjazz.recommendic.appointment.validation.schedule;

import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class ScheduleTimeValidator implements ConstraintValidator<ScheduleTime, String> {
    @Override
    public void initialize(ScheduleTime constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) return true;
        try {
            LocalTime.parse(s);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
