package com.divjazz.recommendic.appointment.validation.schedule.annotation;

import com.divjazz.recommendic.appointment.validation.schedule.ScheduleZoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Documented
@Constraint(validatedBy = ScheduleZoneValidator.class)
public @interface ScheduleZone {
    String message () default "Invalid zone offset";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
