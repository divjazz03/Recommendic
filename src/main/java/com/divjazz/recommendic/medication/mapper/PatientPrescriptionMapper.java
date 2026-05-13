package com.divjazz.recommendic.medication.mapper;

import com.divjazz.recommendic.medication.controller.payload.PatientPrescriptionResponse;
import com.divjazz.recommendic.medication.model.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface PatientPrescriptionMapper extends PrescriptionMapper{
    @Mapping(target = "date", source = "createdAt", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "prescribedBy", source = "prescriberId")
    @Mapping(target = "id", source = "prescriptionId")
    PatientPrescriptionResponse prescriptionToResponse(Prescription prescription);
}
