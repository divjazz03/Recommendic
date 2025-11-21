package com.divjazz.recommendic.appointment.event;

public record AppointmentConfirmedData(
        String appointmentId,
        String consultantId,
        String consultantName,
        String patientId,
        String patientName,
        String startDateTime,
        String endDateTime
)
implements AppointmentEvent{
    @Override
    public AppointmentEventType appointmentEventType() {
        return AppointmentEventType.APPOINTMENT_CONFIRMED;
    }
}
