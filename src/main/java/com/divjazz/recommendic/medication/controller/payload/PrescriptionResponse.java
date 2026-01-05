package com.divjazz.recommendic.medication.controller.payload;

import com.divjazz.recommendic.medication.dto.MedicationDTO;

import java.util.Set;

public record PrescriptionResponse(
        String id,
        Set<MedicationDTO> medications,
        String prescriberId,
        Boolean selfReported
) {}
