package com.divjazz.recommendic.user.dto;


import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;

public record AdminDTO(UserName userName, String email, String number, Gender gender, Address address) {
}
