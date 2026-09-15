package com.divjazz.recommendic.dashboard.controller.payload;

import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.notification.app.enums.NotificationCategory;

import java.util.List;
import java.util.Set;

public record PatientDashboardResponse(
        List<DashboardConsultation> consultationsToday,
        Set<RecentActivity> recentActivities,
        Set<Medication> medications
) implements DashboardResponse {

    public record DashboardConsultation(
            String consultationId,
            String consultantFullName,
            String specialty,
            String dateTime,
            ConsultationChannel channel,
            ConsultationStatus status,
            Boolean isStarted,
            Boolean inEnded,
            String zoneOffset
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
