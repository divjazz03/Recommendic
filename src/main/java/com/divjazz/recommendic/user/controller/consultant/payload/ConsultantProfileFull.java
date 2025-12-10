package com.divjazz.recommendic.user.controller.consultant.payload;

import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import lombok.Builder;

@Builder
public record ConsultantProfileFull(
        UserName userName,
        String email,
        String phoneNumber,
        String dateOfBirth,
        String gender,
        String location,
        Address address,
        String specialty,
        String experience,
        String[] languages,
        String bio,
        String[] subSpecialties,
        String medicalLicenseNumber,
        String medicalCertifications,
        String profileImgUrl
) {
}
