package com.divjazz.recommendic.consultation.mapper;

import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.repository.ConsultationProjection;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ConsultationMapper {
    @Mapping(target = "startTime", source = "startedAt")
    @Mapping(target = "status", source = "consultationStatus")
    @Mapping(target = "patientName",
            source = "appointment.patient.patientProfile.userName.fullName")
    @Mapping(target = "consultantName",
            expression = """
                    java(
                        consultationProjection.appointment().getConsultant().getProfile().getTitle()
                        + " "
                        + consultationProjection.appointment().getConsultant()
                                     .getProfile()
                                     .getUserName()
                                     .getFullName()
                    )
                    """)
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
    ConsultationResponse consultationToConsultationResponse(Consultation consultation);
}
