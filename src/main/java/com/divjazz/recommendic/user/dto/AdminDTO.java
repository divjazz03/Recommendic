package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.Email;
import com.divjazz.recommendic.user.model.userAttributes.PhoneNumber;
import com.divjazz.recommendic.user.model.userAttributes.UserName;

public record AdminDTO(UserName userName, Email email, PhoneNumber number, Address address, String password){
}
