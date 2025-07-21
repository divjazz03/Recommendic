package com.divjazz.recommendic.appointment.validation;

import com.divjazz.recommendic.appointment.domain.DaysOfWeek;
import com.divjazz.recommendic.appointment.validation.annotation.ScheduleDayOfWeeks;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;

public class ScheduleDayOfWeeksValidator implements ConstraintValidator<ScheduleDayOfWeeks, Set<String>> {
    @Override
    public void initialize(ScheduleDayOfWeeks constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Set<String> daysOfWeeks, ConstraintValidatorContext constraintValidatorContext) {
        if (daysOfWeeks == null || daysOfWeeks.isEmpty()) return true;
        try {
            var daysOfWeekSet = daysOfWeeks.stream()
                    .map(DaysOfWeek::fromValue).collect(Collectors.toSet());
            return true;
        } catch (IllegalArgumentException e) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    e.getMessage()
            ).addConstraintViolation();
            return false;
        }
    }
}
