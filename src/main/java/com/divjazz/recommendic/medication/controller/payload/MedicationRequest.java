package com.divjazz.recommendic.medication.controller.payload;

import com.divjazz.recommendic.global.validation.annotation.ValidEnum;
import com.divjazz.recommendic.medication.constants.DurationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MedicationRequest(
        @NotBlank(message = "name is required") String name,
        @NotBlank(message = "Dosage is required") String dosage,
        @NotBlank(message = "frequency is required") String medicationFrequency,
        @NotNull(message = "durationValue is required") @Min(value = 1L, message = "Duration should not be less than 1") Integer durationValue,
        @NotNull(message = "durationType is required") @ValidEnum(enumClass = DurationType.class, message = "durationType is invalid") String durationType,
        @NotBlank(message = "instructions is required") String instructions
) {
}
