package com.divjazz.recommendic.user.mapper;

import com.divjazz.recommendic.user.controller.patient.payload.LifeStyleInfoDTO;
import com.divjazz.recommendic.user.controller.patient.payload.MedicalHistoryDTO;
import com.divjazz.recommendic.user.controller.patient.payload.PatientInfoResponse;
import com.divjazz.recommendic.user.controller.patient.payload.PatientProfileDetails;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.LifeStyleInfo;
import com.divjazz.recommendic.user.model.userAttributes.MedicalHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface PatientMapper {
    LifeStyleInfoDTO toLifeStyleInfoDTO(LifeStyleInfo history);
    @Mapping(target = "knownAllergies", source = "allergies")
    @Mapping(target = "familyMedicalHistory", source = "familyHistory")
    MedicalHistoryDTO toMedicalHistoryDTO(MedicalHistory history);

    @Mapping(target = "firstName", source = "patientProfile.userName.firstName")
    @Mapping(target = "lastName", source = "patientProfile.userName.lastName")
    @Mapping(target = "age", expression = "java(patient.getPatientProfile().getAge())")
    @Mapping(target = "address", source = "patientProfile.address")
    PatientInfoResponse toInfoResponse (Patient patient);

    @Mapping(target = "userName", source = "patientProfile.userName")
    @Mapping(target = "email", source = "userPrincipal.email")
    @Mapping(target = "address", source = "patientProfile.address")
    @Mapping(target = "dateOfBirth", source = "patientProfile.dateOfBirth")
    @Mapping(target = "password", ignore = true)
    PatientDTO toDTO(Patient patient);
    @Mapping(target = "userName", source = "patientProfile.userName")
    @Mapping(target = "email", source = "userPrincipal.email")
    @Mapping(target = "address", source = "patientProfile.address")
    @Mapping(target = "dateOfBirth", source = "patientProfile.dateOfBirth")
    @Mapping(target = "phoneNumber", source = "patientProfile.phoneNumber")
    @Mapping(target = "bloodType", source = "patientProfile.bloodType")
    @Mapping(target = "medicalHistory", source = "patientProfile.medicalHistory")
    @Mapping(target = "lifeStyleInfo", source = "patientProfile.lifeStyleInfo")
    @Mapping(target = "profileImgUrl", source = "patientProfile.profilePicture.pictureUrl")
    @Mapping(target = "userSecuritySetting", source = "patientSecuritySetting")
    @Mapping(target = "interests", source = "medicalCategories", qualifiedByName = "categoryToInterest")
    PatientProfileDetails toPatientProfileDetails(Patient patient);

    @Named("categoryToInterest")
    default Set<String> categoryToInterest (Set<MedicalCategoryEntity> categoryEntities) {
        return categoryEntities.stream()
                .map(MedicalCategoryEntity::getName)
                .collect(Collectors.toSet());
    }
}
