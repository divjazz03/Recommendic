package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserId;

public record PatientInfoResponse(UserId patientId, String lastName, String firstName, String phoneNumber, String gender, Address address) {
}
