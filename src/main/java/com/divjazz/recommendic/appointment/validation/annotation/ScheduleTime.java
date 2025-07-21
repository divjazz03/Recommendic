package com.divjazz.recommendic.appointment.validation.annotation;

import com.divjazz.recommendic.appointment.validation.ScheduleTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = ScheduleTimeValidator.class)
public @interface ScheduleTime {

    String message() default "Unable to parse provided time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
