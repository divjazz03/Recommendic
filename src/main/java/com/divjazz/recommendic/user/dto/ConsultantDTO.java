package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.model.userAttributes.*;

public record ConsultantDTO(UserName userName, Email email, PhoneNumber phoneNumber, Gender gender, Address address, String password) {
}
