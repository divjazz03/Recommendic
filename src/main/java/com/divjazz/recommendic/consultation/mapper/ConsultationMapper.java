package com.divjazz.recommendic.consultation.mapper;

import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.dto.PatientData;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.model.ConsultationPatientData;
import com.divjazz.recommendic.consultation.repository.ConsultationProjection;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ConsultationMapper {
    PatientData toPatientData(ConsultationPatientData patientData);
    @Mapping(target = "startTime", source = "startedAt")
    @Mapping(target = "consultationId", source = "id")
    @Mapping(target = "status", source = "consultationStatus")
    @Mapping(target = "patientName",
            source = "appointment.patient.patientProfile.userName.fullName")
    @Mapping(target = "consultantName",
            source = "appointment.consultant.profile.userName.fullName")
    @Mapping(target = "patientData", source = "appointment.patient.patientData")
    ConsultationResponse consultationToConsultationResponse(ConsultationProjection consultationProjection);
    @Mapping(target = "startTime", source = "startedAt")
    @Mapping(target = "status", source = "consultationStatus")
    @Mapping(target = "patientName",
    source = "consultation.appointment.patient.patientProfile.userName.fullName")
    @Mapping(target = "consultantName",
            expression = """
                    java(
                        consultation.getAppointment().getConsultant().getProfile().getTitle()
                        + " "
                        + consultation.getAppointment().getConsultant()
                                     .getProfile()
                                     .getUserName()
                                     .getFullName()
                    )
                    """)
    @Mapping(target = "patientData", source = "appointment.patient.patientData")
    ConsultationResponse consultationToConsultationResponse(Consultation consultation);
}
