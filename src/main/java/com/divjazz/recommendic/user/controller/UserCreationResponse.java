package com.divjazz.recommendic.user.controller;

import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record UserCreationResponse(String id, String firstName, String lastName, String phoneNumber, Address address) {
}
