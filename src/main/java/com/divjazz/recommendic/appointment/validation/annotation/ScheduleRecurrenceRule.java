package com.divjazz.recommendic.appointment.validation.annotation;

import com.divjazz.recommendic.appointment.validation.ScheduleRecurrenceRuleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Constraint(validatedBy = ScheduleRecurrenceRuleValidator.class)
public @interface ScheduleRecurrenceRule {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
