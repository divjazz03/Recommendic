package com.divjazz.recommendic.user.controller.patient.payload;

import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;

public record PatientProfileFull(
        UserName userName,
        String email,
        String phoneNumber,
        String dateOfBirth,
        String gender,
        Address address,
        String[] interests
) {
}
