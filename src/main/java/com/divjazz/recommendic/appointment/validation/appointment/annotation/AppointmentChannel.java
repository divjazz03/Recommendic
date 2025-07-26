package com.divjazz.recommendic.appointment.validation.appointment.annotation;

import com.divjazz.recommendic.appointment.validation.appointment.AppointmentChannelValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AppointmentChannelValidator.class)
public @interface AppointmentChannel {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
