package com.divjazz.recommendic.appointment.validation.appointment;

import com.divjazz.recommendic.appointment.validation.appointment.annotation.AppointmentChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AppointmentChannelValidator implements ConstraintValidator<AppointmentChannel, String> {
    @Override
    public void initialize(AppointmentChannel constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) return true;

        try {
            ConsultationChannel consultationChannel = ConsultationChannel.valueOf(s.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("%s is not a valid channel".formatted(s))
                    .addConstraintViolation();
            return false;
        }

    }
}
