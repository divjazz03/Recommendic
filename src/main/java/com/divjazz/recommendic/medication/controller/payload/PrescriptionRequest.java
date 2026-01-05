package com.divjazz.recommendic.medication.controller.payload;

import com.divjazz.recommendic.medication.dto.MedicationDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record PrescriptionRequest(
        String consultationId,
    @NotBlank(message = "prescribedTo is required") String prescribedTo,
    @NotBlank(message = "Diagnosis is required") String diagnosis,
    Set<@Valid MedicationRequest> medications,
    @Size(max = 500, message = "notes should have at most 500 characters") String notes
) {
}
