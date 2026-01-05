package com.divjazz.recommendic.medication.mapper;

import com.divjazz.recommendic.medication.controller.payload.PrescriptionResponse;
import com.divjazz.recommendic.medication.dto.MedicationDTO;
import com.divjazz.recommendic.medication.model.Prescription;

import java.util.stream.Collectors;

public final class PrescriptionMapper {

    public static PrescriptionResponse prescriptionToResponse(Prescription prescription) {
        return new PrescriptionResponse(
                prescription.getPrescriptionId(),
                prescription.getMedications().stream().map(medication -> new MedicationDTO(
                        medication.getMedicationId(),
                        medication.getName(),
                        medication.getDosage(),
                        medication.getFrequency(),
                        medication.getStartDate().toString(),
                        medication.getEndDate().toString(),
                        medication.getInstructions(),
                        medication.getMedicationStatus(),
                        medication.getConsultationDate().toString()
                )).collect(Collectors.toSet()),
                prescription.getPrescriberId(),
                prescription.isSelfReported()
        );
    }
}
