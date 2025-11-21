package com.divjazz.recommendic.appointment.event;

public sealed interface AppointmentEvent permits AppointmentCancelledData, AppointmentConfirmedData, AppointmentRequestedData, AppointmentRescheduleAcceptedData, AppointmentRescheduleRequestedData, ConsultantFollowUpAppointmentAcceptedData, ConsultantFollowUpAppointmentRequestedData {
    public AppointmentEventType appointmentEventType();
}
