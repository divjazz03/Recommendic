package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record PatientResponse(
        String patientId,
        String lastName,
        String firstName,
        String phoneNumber,
        String gender,
        Address address
) implements UserResponse {
}
