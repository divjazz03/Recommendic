package com.divjazz.recommendic.global.validation.annotation;

import com.divjazz.recommendic.global.validation.GenderValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GenderValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Gender {
    String message() default "Invalid Gender must be either male or female";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
