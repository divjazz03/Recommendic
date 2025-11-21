package com.divjazz.recommendic.appointment.event;

public record ConsultantFollowUpAppointmentAcceptedData(
        String appointmentId,
        String consultantId,
        String patientId,
        String consultantFullName,
        String patientFullName,
        String startDateTime,
        String endDateTime,
        String status
) implements AppointmentEvent {
    @Override
    public AppointmentEventType appointmentEventType() {
        return AppointmentEventType.CONSULTANT_FOLLOWUP_APPOINTMENT_CONFIRMED;
    }
}
