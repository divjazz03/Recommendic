package com.divjazz.recommendic.appointment.validation.annotation;

import com.divjazz.recommendic.appointment.validation.ScheduleChannelValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Constraint(validatedBy = ScheduleChannelValidation.class)
public @interface ScheduleChannel {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
