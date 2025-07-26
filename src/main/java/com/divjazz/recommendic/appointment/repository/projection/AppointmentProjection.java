package com.divjazz.recommendic.appointment.repository.projection;

import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;

public record AppointmentProjection (
        Appointment appointment
) {
}
