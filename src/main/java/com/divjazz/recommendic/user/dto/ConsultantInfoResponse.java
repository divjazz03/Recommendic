package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.userAttributes.Address;

public record ConsultantInfoResponse(
        String consultantId,
        String lastName,
        String firstName,
        String gender,
        Address address,
        String medicalSpecialization
) {
}
