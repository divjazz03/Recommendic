package com.divjazz.recommendic.user.controller.admin;

public record AdminRequestParams(
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String gender,
    String zipcode,
    String city,
    String state,
    String country
){}
