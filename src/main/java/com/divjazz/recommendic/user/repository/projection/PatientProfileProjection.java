package com.divjazz.recommendic.user.repository.projection;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.UserName;

import java.time.LocalDate;
import java.util.Set;

public interface PatientProfileProjection {

    UserName getUserName();
    String getEmail();
    String getPhoneNumber();
    LocalDate getDateOfBirth();
    Gender getGender();
    Address getAddress();
    Set<MedicalCategoryProjection> getMedicalCategories();
    ProfilePicture getProfilePicture();
}
