package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.model.userAttributes.Address;

public record PatientInfoResponse(String patientId, String lastName, String firstName, String phoneNumber, String gender, Address address) {
}
