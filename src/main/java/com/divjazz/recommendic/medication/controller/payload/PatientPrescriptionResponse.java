package com.divjazz.recommendic.medication.controller.payload;

import com.divjazz.recommendic.medication.constants.PrescriptionStatus;
import com.divjazz.recommendic.medication.dto.MedicationDTO;

import java.util.Set;

public record PatientPrescriptionResponse(
        String id,
        String diagnosis,
        String date,
        PrescriptionStatus status,
        String notes,
        Set<MedicationDTO> medications,
        String prescriberBy,
        Boolean selfReported
) implements PrescriptionResponse{}
