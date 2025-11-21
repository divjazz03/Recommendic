package com.divjazz.recommendic.appointment.event;

public record AppointmentRequestedData(
        String appointmentId,
        String patientId,
        String patientName,
        String consultantName,
        String consultantId,
        String startDateTime,
        String endDateTime
) implements AppointmentEvent {
    @Override
    public AppointmentEventType appointmentEventType() {
        return AppointmentEventType.APPOINTMENT_REQUESTED;
    }
}
