package com.divjazz.recommendic.global.validation.annotation;

import com.divjazz.recommendic.global.validation.DateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = DateValidator.class)
public @interface ValidDate {
    String message() default "Unable to parse provided date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
