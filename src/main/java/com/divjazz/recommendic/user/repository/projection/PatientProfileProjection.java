package com.divjazz.recommendic.user.repository.projection;

import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.enums.BloodType;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.*;

import java.time.LocalDate;
import java.util.Set;

public record PatientProfileProjection(
        UserName userName,
        String email,
        LocalDate dateOfBirth,
        Gender gender,
        Address address,
        String phoneNumber,
        Set<MedicalCategoryProjection> medicalCategories,
        ProfilePicture profilePicture,
        BloodType bloodType,
        LifeStyleInfo lifeStyleInfo,
        MedicalHistory medicalHistory
) {
}
