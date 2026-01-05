package com.divjazz.recommendic.global.validation;

import com.divjazz.recommendic.global.validation.annotation.ValidZone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.util.Objects;

public class ZoneValidator implements ConstraintValidator<ValidZone, String> {
    @Override
    public void initialize(ValidZone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(Objects.isNull(s) || s.isBlank()) return true;
        try {
            java.time.ZoneOffset.of(s);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}
