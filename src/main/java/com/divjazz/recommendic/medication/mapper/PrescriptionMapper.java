package com.divjazz.recommendic.medication.mapper;

import com.divjazz.recommendic.medication.controller.payload.ConsultantPrescriptionResponse;
import com.divjazz.recommendic.medication.controller.payload.PatientPrescriptionResponse;
import com.divjazz.recommendic.medication.dto.MedicationDTO;
import com.divjazz.recommendic.medication.model.Medication;
import com.divjazz.recommendic.medication.model.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.stream.Collectors;


public interface PrescriptionMapper {
    @Mapping(target = "id", source = "medicationId")
    MedicationDTO toMedicationDTO(Medication medication);
}
