package com.divjazz.recommendic.user.controller.patient;


    public record PatientRequestParams(String firstName,
                                       String lastName,
                                       String email,
                                       String password,
                                       String phoneNumber,
                                       String gender,
                                       String zipCode,
                                       String city,
                                       String state,
                                       String country) {}
