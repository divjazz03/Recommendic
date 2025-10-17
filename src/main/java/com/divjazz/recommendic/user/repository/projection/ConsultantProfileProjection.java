package com.divjazz.recommendic.user.repository.projection;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.certification.ConsultantEducation;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.UserName;

import java.time.LocalDate;
import java.util.Set;

public interface ConsultantProfileProjection {
    UserName getUserName();
    String getEmail();
    String getPhoneNumber();
    LocalDate getDateOfBirth();
    Gender getGender();
    String getLocation();
    Address getAddress();
    MedicalCategoryProjection getSpecialty();
    int getExperience();
    String[] getLanguages();
    String getBio();
    Set<ConsultantEducationProjection> getEducations();
    ProfilePicture getProfilePicture();
}
