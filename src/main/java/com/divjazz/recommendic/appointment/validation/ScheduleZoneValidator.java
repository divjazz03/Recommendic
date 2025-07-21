package com.divjazz.recommendic.appointment.validation;

import com.divjazz.recommendic.appointment.validation.annotation.ScheduleZone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;

public class ScheduleZoneValidator implements ConstraintValidator<ScheduleZone, String> {
    @Override
    public void initialize(ScheduleZone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            java.time.ZoneOffset.of(s);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}
