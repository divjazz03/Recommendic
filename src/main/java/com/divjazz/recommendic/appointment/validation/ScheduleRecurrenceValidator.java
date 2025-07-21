package com.divjazz.recommendic.appointment.validation;

import com.divjazz.recommendic.appointment.dto.ScheduleCreationRequest;
import com.divjazz.recommendic.appointment.validation.annotation.ScheduleRecurrence;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ScheduleRecurrenceValidator implements ConstraintValidator<ScheduleRecurrence, ScheduleCreationRequest> {
    @Override
    public void initialize(ScheduleRecurrence constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ScheduleCreationRequest scheduleCreationRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (scheduleCreationRequest != null) {
            if (scheduleCreationRequest.isRecurring() && scheduleCreationRequest.recurrenceRule() == null) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(
                        "If schedule is recurring, the recurrence rule must not be null"
                ).addConstraintViolation();
                return false;

            }
            return true;
        }
        return false;
    }
}
