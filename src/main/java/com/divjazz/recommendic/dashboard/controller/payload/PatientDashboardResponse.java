package com.divjazz.recommendic.dashboard.controller.payload;

import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.notification.app.enums.NotificationCategory;

import java.util.Set;

public record PatientDashboardResponse(
        Set<DashboardAppointment> appointmentsToday,
        Set<RecentActivity> recentActivities,
        Set<Medication> medications
) implements DashboardResponse {

    public record DashboardAppointment(
            String appointmentId,
            String consultantFullName,
            String specialty,
            String dateTime,
            ConsultationChannel channel,
            AppointmentStatus status
    ){}
    public record RecentActivity(
            String title,
            String dateTime,
            NotificationCategory context //prescription, result, appointment
    ){}
    public record Medication(
            String name,
            String dosageQuantity,
            String dosageFrequency,
            String nextDoseDateTime
    ){}
}
