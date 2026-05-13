package com.divjazz.recommendic.medication.mapper;

import com.divjazz.recommendic.medication.controller.payload.ConsultantPrescriptionResponse;
import com.divjazz.recommendic.medication.model.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ConsultantPrescriptionMapper extends PrescriptionMapper{
    @Mapping(target = "patientName", source = "prescribedTo.patientProfile.userName.fullName")
    @Mapping(target = "gender", source = "prescribedTo.gender")
    @Mapping(target = "patientAge", expression = "java(prescription.getPrescribedTo().getPatientProfile().getAge())")
    @Mapping(target = "date", source = "createdAt", dateFormat = "yyyy-MM-dd")
    ConsultantPrescriptionResponse prescriptionToResponse(Prescription prescription);
}
