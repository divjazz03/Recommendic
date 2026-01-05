package com.divjazz.recommendic.global.validation;

import com.divjazz.recommendic.global.validation.annotation.ValidTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class TimeValidator implements ConstraintValidator<ValidTime, String> {
    @Override
    public void initialize(ValidTime constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(s) || s.isBlank()) return true;
        try {
            LocalTime.parse(s);
            return true;
        } catch (DateTimeParseException e) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(e.getMessage()).addConstraintViolation();
            return false;
        }
    }
}
