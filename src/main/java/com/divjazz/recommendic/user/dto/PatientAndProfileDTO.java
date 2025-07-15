package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;

public record PatientAndProfileDTO(
        Patient patient,
        PatientProfile patientProfile
) {
}
