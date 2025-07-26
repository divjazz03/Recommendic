package com.divjazz.recommendic.appointment.validation.schedule.annotation;

import com.divjazz.recommendic.appointment.validation.schedule.ScheduleDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = ScheduleDateValidator.class)
public @interface ScheduleDate {
    String message() default "Unable to parse provided date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
