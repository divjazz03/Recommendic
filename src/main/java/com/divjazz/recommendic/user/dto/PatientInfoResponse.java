package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record PatientInfoResponse(
        Long patientId,
        String lastName,
        String firstName,
        String phoneNumber,
        String gender,
        Address address
) implements UserInfoResponse{
}
