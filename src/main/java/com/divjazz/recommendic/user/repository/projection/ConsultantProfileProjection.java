package com.divjazz.recommendic.user.repository.projection;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.UserName;

import java.time.LocalDate;
import java.util.Set;

public record ConsultantProfileProjection (
    UserName userName,
    String email,
    String phoneNumber,
    LocalDate dateOfBirth,
    Gender gender,
    String location,
    Address address,
    MedicalCategoryProjection specialty,
    int experience,
    String[] languages,
    String bio,
    Set<ConsultantEducationProjection> educations,
    ProfilePicture profilePicture
){}
