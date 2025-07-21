package com.divjazz.recommendic.global.validation;

import com.divjazz.recommendic.global.validation.annotation.Gender;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<Gender, String> {
    @Override
    public void initialize(Gender constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        for (com.divjazz.recommendic.user.enums.Gender gender: com.divjazz.recommendic.user.enums.Gender.values()) {
            if (value.equalsIgnoreCase(gender.toString())) {
                return true;
            }
        }
        return false;
    }
}
