package com.divjazz.recommendic.user.controller.consultant;

public record ConsultantRegistrationParams(String firstName,
                                           String lastName,
                                           String email,
                                           String password,
                                           String phoneNumber,
                                           String gender,
                                           String zipCode,
                                           String city,
                                           String state,
                                           String country
                                      ) {
}
