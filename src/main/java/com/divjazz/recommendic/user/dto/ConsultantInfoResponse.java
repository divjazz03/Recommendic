package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@JsonInclude(NON_DEFAULT)
public record ConsultantInfoResponse(
        Long consultantId,
        String lastName,
        String firstName,
        String gender,
        Address address,
        String medicalSpecialization
) implements UserInfoResponse{
}
