package com.divjazz.recommendic.user.mapper;

import com.divjazz.recommendic.user.controller.patient.payload.PatientInfoResponse;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface PatientMapper {
    @Mapping(target = "firstName", source = "patientProfile.userName.firstName")
    @Mapping(target = "lastName", source = "patientProfile.userName.lastName")
    @Mapping(target = "age", expression = "java(patient.getPatientProfile().getAge())")
    @Mapping(target = "address", source = "patientProfile.address")
    PatientInfoResponse toInfoResponse (Patient patient);
    @Mapping(target = "userName", source = "patientProfile.userName")
    @Mapping(target = "email", source = "userPrincipal.email")
    @Mapping(target = "address", source = "patientProfile.address")
    @Mapping(target = "dateOfBirth", source = "patientProfile.dateOfBirth")
    PatientDTO toDTO(Patient patient);
}
