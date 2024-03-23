package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.*;

public record ConsultantDTO(UserName userName, String email, String phoneNumber, Gender gender, Address address, String password) {
}
