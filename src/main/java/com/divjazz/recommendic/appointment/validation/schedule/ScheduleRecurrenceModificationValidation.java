package com.divjazz.recommendic.appointment.validation.schedule;

import com.divjazz.recommendic.appointment.dto.ScheduleModificationRequest;
import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleRecurrenceModification;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class ScheduleRecurrenceModificationValidation implements ConstraintValidator<ScheduleRecurrenceModification, ScheduleModificationRequest> {
    @Override
    public boolean isValid(ScheduleModificationRequest modificationRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(modificationRequest)) return false;

        if (modificationRequest.isRecurring() && modificationRequest.recurrenceRule() == null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    "If schedule is recurring, the recurrence rule must not be null"
            ).addConstraintViolation();
            return false;
        }
        return true;
    }
}
