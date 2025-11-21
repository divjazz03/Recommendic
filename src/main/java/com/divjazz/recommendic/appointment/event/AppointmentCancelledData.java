package com.divjazz.recommendic.appointment.event;

public record AppointmentCancelledData(
        String appointmentId,
        String patientId,
        String consultantId,
        String patientName,
        String consultantName,
        String startDateTime,
        String endDateTime,
        String reason
) implements AppointmentEvent{
    @Override
    public AppointmentEventType appointmentEventType() {
        return AppointmentEventType.APPOINTMENT_CANCELLED;
    }
}
