package com.divjazz.recommendic.user.repository.projection;

import com.divjazz.recommendic.user.model.userAttributes.Address;

public record ConsultantInfoProjection(
        String consultantId,
        String lastName,
        String firstName,
        String gender,
        String phoneNumber,
        String address,
        String medicalSpecialization
) {

}
