package com.divjazz.recommendic.appointment.event;

public record AppointmentRescheduleRequestedData(
        String appointmentId,
        String patientId,
        String patientName,
        String consultantName,
        String consultantId,
        String previousDateTime,
        String proposedDateTime
) implements AppointmentEvent {
    @Override
    public AppointmentEventType appointmentEventType() {
        return null;
    }
}
