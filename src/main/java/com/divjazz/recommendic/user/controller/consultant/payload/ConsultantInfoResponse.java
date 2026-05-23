package com.divjazz.recommendic.user.controller.consultant.payload;

import com.divjazz.recommendic.user.domain.OnboardingStage;
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
        OnboardingStage onboardingStage,
        String medicalSpecialization
) {

    public ConsultantInfoResponse(String consultantId,
                                  String lastName,
                                  String firstName,
                                  String gender,
                                  String age,
                                  OnboardingStage onboardingStage,
                                  Address address) {
        this(consultantId,lastName,firstName,gender,age,address,onboardingStage,null);
    }
}
