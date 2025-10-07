package com.divjazz.recommendic.appointment.validation.schedule;

import com.divjazz.recommendic.appointment.domain.RecurrenceFrequency;
import com.divjazz.recommendic.appointment.controller.payload.RecurrenceRuleRequest;
import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleRecurrenceRule;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ScheduleRecurrenceRuleValidator implements ConstraintValidator<ScheduleRecurrenceRule, RecurrenceRuleRequest> {

    @Override
    public void initialize(ScheduleRecurrenceRule constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(RecurrenceRuleRequest recurrenceRule, ConstraintValidatorContext constraintValidatorContext) {
            if (recurrenceRule == null) return true;
            try {
                var recurrence = recurrenceRule.frequency();
                boolean recurrenceIsNotDailyAndWeekdayIsNullOrEmpty = RecurrenceFrequency.WEEKLY.equals(recurrence)
                        && (recurrenceRule.weekDays() == null
                        || recurrenceRule.weekDays().isEmpty());

                if (recurrenceIsNotDailyAndWeekdayIsNullOrEmpty) {
                        constraintValidatorContext.disableDefaultConstraintViolation();
                        constraintValidatorContext.buildConstraintViolationWithTemplate(
                                "if recurrence frequency is weekly then weekdays must have at least one element"
                        ).addConstraintViolation();
                        return false;
                }
                return true;
            } catch (IllegalArgumentException e) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(e.getMessage())
                        .addConstraintViolation();
                return false;
            }

    }
}
