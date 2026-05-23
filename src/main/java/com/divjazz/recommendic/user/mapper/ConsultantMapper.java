package com.divjazz.recommendic.user.mapper;

import com.divjazz.recommendic.user.controller.consultant.payload.ConsultantInfoResponse;
import com.divjazz.recommendic.user.controller.consultant.payload.ConsultantProfileDetails;
import com.divjazz.recommendic.user.controller.consultant.payload.ConsultantProfileFull;
import com.divjazz.recommendic.user.dto.ConsultantMinimal;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ConsultantMapper {
    @Mapping(target = "profileImgUrl", source = "profile.profilePicture.pictureUrl")
    @Mapping(target = "location", source = "profile.locationOfInstitution")
    @Mapping(target = "medicalLicenseNumber", source = "profile.licenseNumber")
    @Mapping(target = "experience", source = "profile.yearsOfExperience")
    @Mapping(target = "email", source = "userPrincipal.email")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "specialty", source = "specialization.name")
    @Mapping(target = "userName", source = "profile.userName")
    ConsultantProfileFull toConsultantProfileFull(Consultant consultant);
    @Mapping(target = "consultantId", source = "userId")
    @Mapping(target = "firstName", source = "consultant.profile.userName.firstName")
    @Mapping(target = "lastName", source = "consultant.profile.userName.lastName")
    @Mapping(target = "age", expression = "java(consultant.getProfile().getAge())")
    @Mapping(target = "address", source = "consultant.profile.address")
    @Mapping(target = "medicalSpecialization", source = "consultant.specialization.name")
    ConsultantInfoResponse toInfoResponse(Consultant consultant);


}
