package com.divjazz.recommendic.appointment.validation.schedule.annotation;

import com.divjazz.recommendic.appointment.validation.schedule.ScheduleDayOfWeeksValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ScheduleDayOfWeeksValidator.class)
public @interface ScheduleDayOfWeeks {
    String message() default "Failure validating day of weeks";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
