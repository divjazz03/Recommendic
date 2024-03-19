package com.divjazz.recommendic.user.controller;


    public record PatientRequestParams(String firstName,
                                       String lastName,
                                       String email,
                                       String phoneNumber,
                                       String gender,
                                       String zipCode,
                                       String city,
                                       String state,
                                       String country) {}

