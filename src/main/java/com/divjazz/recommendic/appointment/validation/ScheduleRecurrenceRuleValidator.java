package com.divjazz.recommendic.appointment.validation;

import com.divjazz.recommendic.appointment.domain.RecurrenceFrequency;
import com.divjazz.recommendic.appointment.domain.RecurrenceRule;
import com.divjazz.recommendic.appointment.validation.annotation.ScheduleRecurrenceRule;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ScheduleRecurrenceRuleValidator implements ConstraintValidator<ScheduleRecurrenceRule, RecurrenceRule> {

    @Override
    public void initialize(ScheduleRecurrenceRule constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(RecurrenceRule recurrenceRule, ConstraintValidatorContext constraintValidatorContext) {
            if (recurrenceRule == null) return true;
            try {
                var recurrence = recurrenceRule.frequency();
                boolean recurrenceIsNotDailyAndWeekdayIsNullOrEmpty = !RecurrenceFrequency.DAILY.equals(recurrence)
                        && (recurrenceRule.weekDays() == null
                        || recurrenceRule.weekDays().isEmpty());

                if (recurrenceIsNotDailyAndWeekdayIsNullOrEmpty) {
                        constraintValidatorContext.disableDefaultConstraintViolation();
                        constraintValidatorContext.buildConstraintViolationWithTemplate(
                                "if recurrence frequency is not daily then weekdays must have at least one element"
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
