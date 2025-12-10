package com.divjazz.recommendic.user.controller.patient.payload;

import com.divjazz.recommendic.user.enums.BloodType;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record PatientProfileDetails(
        UserName userName,
        String email,
        String phoneNumber,
        String dateOfBirth,
        String gender,
        Address address,
        Set<String> interests,
        BloodType bloodType,
        MedicalHistoryDTO medicalHistory,
        LifeStyleInfoDTO lifeStyleInfo,
        String profileImgUrl
) {
}
