package com.divjazz.recommendic.consultation.mapper;

import com.divjazz.recommendic.consultation.dto.*;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.model.ConsultationPatientData;
import com.divjazz.recommendic.consultation.repository.ConsultationProjection;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ConsultationMapper {
    @Mapping(target = "startTime", source = "startedAt")
    @Mapping(target = "consultationId", source = "id")
    @Mapping(target = "status", source = "consultationStatus")
    @Mapping(target = "patientName",
            source = "appointment.patient.patientProfile.userName.fullName")
    @Mapping(target = "consultantName",
            source = "appointment.consultant.profile.userName.fullName")
    @Mapping(target = "patientData", ignore = true)
    ConsultationResponse consultationToConsultationResponse(ConsultationProjection consultationProjection);

    @Mapping(target = "startTime", source = "consultation.appointment.startDateAndTime")
    @Mapping(target = "endTime", source = "consultation.appointment.endDateAndTime")
    @Mapping(target = "startedAt", source = "consultation.startedAt")
    @Mapping(target = "endedAt", source = "consultation.endedAt")
    @Mapping(target = "status", source = "consultation.consultationStatus")
    @Mapping(target = "patientName",
            source = "consultation.appointment.patient.patientProfile.userName.fullName")
    @Mapping(target = "consultantName",
            source = "consultation.appointment.consultant.profile.userName.fullName")
    @Mapping(target = "patientId", source = "patient.userId")
    @Mapping(target = "consultantId", source = "consultant.userId")
    @Mapping(target = "patientData", source = "patient", qualifiedByName = "patientToPatientData")
    @Mapping(target = "consultantData", source = "consultant", qualifiedByName = "consultantToConsultantData")
    ConsultationResponse consultationToConsultationResponseWithPatientData(Consultation consultation, Patient patient, Consultant consultant);

    @Mapping(target = "startTime", source = "consultation.appointment.startDateAndTime")
    @Mapping(target = "endTime", source = "consultation.appointment.endDateAndTime")

    @Mapping(target = "startedAt", source = "consultation.startedAt")
    @Mapping(target = "endedAt", source = "consultation.endedAt")
    @Mapping(target = "consultationId", source = "consultationId")
    @Mapping(target = "status", source = "consultationStatus")
    @Mapping(target = "reason", source = "appointment.reason")
    @Mapping(target = "appointmentId", source = "appointment.appointmentId")
    @Mapping(target = "patientData",
            source = "appointment.patient", qualifiedByName = "patientToPatientDataMinimal")
    @Mapping(target = "consultantData", source = "appointment.consultant", qualifiedByName = "consultantToConsultantData")
    ConsultationResponseMinimal consultationToConsultationResponseMinimal(Consultation consultation);

    @Named("patientToPatientDataMinimal")
    default PatientDataMinimal patientToPatientDataMinimal(Patient patient) {
        Optional<ConsultationPatientData> patientData = Optional.ofNullable(patient.getPatientData());
        return PatientDataMinimal.builder()
                .age(patient.getPatientProfile().getAge())
                .name(patient.getPatientProfile().getUserName().getFullName())
                .gender(patient.getGender())
                .id(patient.getUserId())
                .bloodType(patient.getPatientProfile().getBloodType())
                .profileUrl(patient.getPatientProfile().getProfilePicture().getPictureUrl())
                .build();
    }
    @Named("patientToPatientData")
    default PatientData patientToPatientData(Patient patient) {
        Optional<ConsultationPatientData> patientData = Optional.ofNullable(patient.getPatientData());
        return PatientData.builder()
                .age(patient.getPatientProfile().getAge())
                .name(patient.getPatientProfile().getUserName().getFullName())
                .gender(patient.getGender())
                .conditions(patientData.map(ConsultationPatientData::getConditions).orElse(List.of()))
                .connectedDevices(patientData.map(ConsultationPatientData::getConnectedDevices).orElse(List.of()))
                .insurance(patientData.map(ConsultationPatientData::getInsurance).orElse(null))
                .lastRecordedVitals(patientData.map(ConsultationPatientData::getLastRecordedVitals).orElse(null))
                .patientReported(patientData.map(ConsultationPatientData::getPatientReported).orElse(null))
                .lastVisit(patientData.map(ConsultationPatientData::getLastVisit).map(LocalDateTime::toString).orElse(null))
                .allergies(patientData.map(ConsultationPatientData::getAllergies).orElse(List.of()))
                .bloodType(patient.getPatientProfile().getBloodType())
                .email(patient.getUserPrincipal().getEmail())
                .phoneNumber(patient.getPatientProfile().getPhoneNumber())
                .build();
    }

    @Named("consultantToConsultantData")
    default ConsultantData consultantToConsultantData(Consultant consultant) {
        return ConsultantData.builder()
                .image(Optional.ofNullable(
                                consultant.getProfile().getProfilePicture())
                        .map(ProfilePicture::getPictureUrl).orElse(null))
                .title(consultant.getProfile().getTitle())
                .location(consultant.getProfile().getLocationOfInstitution())
                .experience(consultant.getProfile().getYearsOfExperience())
                .rating(4.5F)
                .gender(consultant.getGender())
                .name(consultant.getProfile().getUserName().getFullName())
                .specialty(consultant.getSpecialization().getName())
                .build();
    }
}
