package com.divjazz.recommendic.appointment.event;

public record AppointmentRescheduleAcceptedData(
        String appointmentId,
        String patientId,
        String consultantId,
        String patientFullName,
        String consultantFullName,
        String previousDateTime,
        String proposedDateTime,
        String channel
) implements AppointmentEvent {
    @Override
    public AppointmentEventType appointmentEventType() {
        return AppointmentEventType.APPOINTMENT_RESCHEDULE_ACCEPTED;
    }
}
