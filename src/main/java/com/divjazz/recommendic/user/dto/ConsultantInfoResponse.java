package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserId;

import java.util.Set;

public record ConsultantInfoResponse(UserId consultantId, String lastName, String firstName, String gender, Address address, MedicalCategory medicalSpecialization) {
}
