package com.divjazz.recommendic.appointment.validation;

import com.divjazz.recommendic.appointment.validation.annotation.ScheduleDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ScheduleDateValidator implements ConstraintValidator<ScheduleDate, String> {
    @Override
    public void initialize(ScheduleDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null)
            return true;
        try {
            LocalDate.parse(s);
            return true;
        } catch (DateTimeParseException e) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(e.getMessage())
                    .addConstraintViolation();
            return false;
        }
    }
}
