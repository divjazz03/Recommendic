package com.divjazz.recommendic.user.mapper;

import com.divjazz.recommendic.user.controller.consultant.payload.ConsultantInfoResponse;
import com.divjazz.recommendic.user.model.Consultant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ConsultantMapper {
    @Mapping(target = "consultantId", source = "userId")
    @Mapping(target = "firstName", source = "consultant.profile.userName.firstName")
    @Mapping(target = "lastName", source = "consultant.profile.userName.lastName")
    @Mapping(target = "age", expression = "java(consultant.getProfile().getAge())")
    @Mapping(target = "address", source = "consultant.profile.address")
    @Mapping(target = "medicalSpecialization", source = "consultant.specialization.name")
    ConsultantInfoResponse toInfoResponse(Consultant consultant);
}
