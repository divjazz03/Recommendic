package com.divjazz.recommendic.consultation.validation;

import com.divjazz.recommendic.consultation.dto.ConsultationCompleteRequest;
import com.divjazz.recommendic.consultation.validation.annotation.ConsultationCompleteValidationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class ConsultationCompleteRequestValidator implements ConstraintValidator<ConsultationCompleteValidationRequest, ConsultationCompleteRequest> {
    @Override
    public boolean isValid(ConsultationCompleteRequest consultationCompleteRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(consultationCompleteRequest)) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Request is null")
                    .addConstraintViolation();
            return false;
        }
        if (consultationCompleteRequest.shouldReschedule()) {
            boolean requestHasNoRescheduleDate = Objects.isNull(consultationCompleteRequest.date());
            boolean requestHasNoScheduleId = Objects.isNull(consultationCompleteRequest.scheduleId());
            boolean requestHasNoRescheduleDateOrScheduleId = requestHasNoRescheduleDate || requestHasNoScheduleId;

            if (requestHasNoRescheduleDateOrScheduleId) {
                if (requestHasNoRescheduleDate) {
                    constraintValidatorContext.buildConstraintViolationWithTemplate("Please provide a date for another schedule")
                            .addConstraintViolation();
                }
                if (requestHasNoScheduleId) {
                    constraintValidatorContext.buildConstraintViolationWithTemplate("Please provide schedule id for the target schedule")
                            .addConstraintViolation();
                }
                return false;
            }
        }
        return true;
    }
}
