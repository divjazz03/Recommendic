package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.model.userAttributes.Gender;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import com.divjazz.recommendic.user.model.userAttributes.UserName;

public record PatientDTO(UserId id, String firstName, String lastName, String phoneNumber, Gender gender) {}
