package com.divjazz.recommendic.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class DateOfBirthValidator implements ConstraintValidator<ValidDateOfBirth, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(s) || s.isEmpty() || s.isBlank()) return true;
        try {
             var dateOfBirth = LocalDate.parse(s);
             if (isBelow18Years(dateOfBirth)) {
                 constraintValidatorContext.buildConstraintViolationWithTemplate("The user must not be below 18 years")
                         .addConstraintViolation();
                 return false;
             }
             return true;

        } catch (DateTimeParseException e) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid dateOfBirth: %s".formatted(s))
                    .addConstraintViolation();
            return false;
        }
    }

    private boolean isBelow18Years(LocalDate date) {
        var nowMinus18Years = LocalDate.now().minusYears(18);
        return date.isAfter(nowMinus18Years);
    }
}
