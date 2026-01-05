package com.divjazz.recommendic.global.validation.annotation;

import com.divjazz.recommendic.global.validation.ZoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Documented
@Constraint(validatedBy = ZoneValidator.class)
public @interface ValidZone {
    String message () default "Invalid zone offset";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
