package com.divjazz.recommendic.user.controller.patient.payload;

import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;
@JsonIgnoreProperties(ignoreUnknown = true)
public record PatientProfileUpdateRequest(
        UserName userName,
        String phoneNumber,
        String dateOfBirth,
        Address address,
        Set<String> interests,
        String profileImgUrl
) {
}
