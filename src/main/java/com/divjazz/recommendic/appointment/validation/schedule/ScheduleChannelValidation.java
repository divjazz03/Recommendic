package com.divjazz.recommendic.appointment.validation.schedule;

import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;

public class ScheduleChannelValidation implements ConstraintValidator<ScheduleChannel, Set<String>> {
    @Override
    public void initialize(ScheduleChannel constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Set<String> stringSet, ConstraintValidatorContext constraintValidatorContext) {
        if (stringSet == null) return true;
        if (stringSet.isEmpty()) return true;
        try {
            Set<ConsultationChannel> set = stringSet.stream()
                    .map(name -> ConsultationChannel.valueOf(name.toUpperCase()))
                    .collect(Collectors.toSet());
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
