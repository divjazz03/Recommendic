package com.divjazz.recommendic.appointment.validation.schedule;

import com.divjazz.recommendic.appointment.domain.RecurrenceFrequency;
import com.divjazz.recommendic.appointment.controller.payload.RecurrenceRuleRequest;
import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleRecurrenceRule;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class ScheduleRecurrenceRuleValidator implements ConstraintValidator<ScheduleRecurrenceRule, RecurrenceRuleRequest> {

    @Override
    public void initialize(ScheduleRecurrenceRule constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(RecurrenceRuleRequest recurrenceRule, ConstraintValidatorContext constraintValidatorContext) {
            if (recurrenceRule == null) return true;

            boolean validEndDate;

            try {
                validEndDate = Objects.nonNull(recurrenceRule.endDate());
                LocalDate.parse(recurrenceRule.endDate());
            } catch (DateTimeParseException | NullPointerException e) {
                validEndDate = false;
            }
            try {
                var recurrence = recurrenceRule.frequency();

                boolean recurrenceIsWeeklyAndWeekdayIsNullOrEmpty = RecurrenceFrequency.WEEKLY.equals(recurrence)
                        && (recurrenceRule.weekDays() == null
                        || recurrenceRule.weekDays().isEmpty());

                boolean recurrenceIsOneOffButNoEndDate = RecurrenceFrequency.ONE_OFF.equals(recurrence)
                        && !validEndDate;

                if (recurrenceIsWeeklyAndWeekdayIsNullOrEmpty) {
                        constraintValidatorContext.disableDefaultConstraintViolation();
                        constraintValidatorContext.buildConstraintViolationWithTemplate(
                                "if recurrence frequency is weekly then weekdays must have at least one element"
                        ).addConstraintViolation();
                        return false;
                }

                if (recurrenceIsOneOffButNoEndDate) {
                    constraintValidatorContext.disableDefaultConstraintViolation();
                    constraintValidatorContext.buildConstraintViolationWithTemplate(
                            "If recurrence frequency is one-off then you must provide a valid end date"
                    ).addConstraintViolation();
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
