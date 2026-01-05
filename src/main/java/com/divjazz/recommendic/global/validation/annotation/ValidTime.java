package com.divjazz.recommendic.global.validation.annotation;

import com.divjazz.recommendic.global.validation.TimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = TimeValidator.class)
public @interface ValidTime {

    String message() default "Unable to parse provided time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
