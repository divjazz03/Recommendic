package com.divjazz.recommendic.consultation.validation.annotation;

import com.divjazz.recommendic.consultation.validation.ConsultationCompleteRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = ConsultationCompleteRequestValidator.class)
public @interface ConsultationCompleteValidationRequest {
    String message() default "Request is Invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
