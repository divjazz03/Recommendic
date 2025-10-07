package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record PatientDTO(
        UserName userName,
        String email,
        String dateOfBirth,
        Gender gender,
        Address address,
        String password
) {
}
