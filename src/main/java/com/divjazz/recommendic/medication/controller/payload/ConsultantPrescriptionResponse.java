package com.divjazz.recommendic.medication.controller.payload;

import com.divjazz.recommendic.medication.constants.PrescriptionStatus;
import com.divjazz.recommendic.medication.dto.MedicationDTO;

import java.util.Set;

public record ConsultantPrescriptionResponse(
        String id,
        String patientName,
        String patientAge,
        String gender,
        String diagnosis,
        Set<MedicationDTO> medications,
        String prescriberId,
        String prescriberName,
        String date,
        PrescriptionStatus status,
        String notes
) implements PrescriptionResponse{

}
