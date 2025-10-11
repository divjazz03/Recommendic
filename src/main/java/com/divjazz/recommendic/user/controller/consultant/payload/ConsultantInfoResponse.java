package com.divjazz.recommendic.user.controller.consultant.payload;

import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@JsonInclude(NON_DEFAULT)
public record ConsultantInfoResponse(
        String consultantId,
        String lastName,
        String firstName,
        String gender,
        String age,
        Address address,
        String medicalSpecialization
) {

    public ConsultantInfoResponse(String consultantId,
                                  String lastName,
                                  String firstName,
                                  String gender,
                                  String age,
                                  Address address) {
        this(consultantId,lastName,firstName,gender,age,address,null);
    }
}
