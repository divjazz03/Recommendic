package com.divjazz.recommendic.appointment.validation.schedule.annotation;

import com.divjazz.recommendic.appointment.validation.schedule.ScheduleRecurrenceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ScheduleRecurrenceValidator.class)
public @interface ScheduleRecurrence {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
