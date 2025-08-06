package com.divjazz.recommendic.appointment.validation.schedule;

import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleZone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.util.Objects;

public class ScheduleZoneValidator implements ConstraintValidator<ScheduleZone, String> {
    @Override
    public void initialize(ScheduleZone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(Objects.isNull(s)) return true;
        try {
            java.time.ZoneOffset.of(s);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}
