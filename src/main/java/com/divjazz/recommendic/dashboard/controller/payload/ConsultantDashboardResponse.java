package com.divjazz.recommendic.dashboard.controller.payload;

import com.divjazz.recommendic.consultation.enums.ConsultationChannel;

import java.util.Set;

public record ConsultantDashboardResponse(
        int todayAppointmentsCount,
        int appointmentNumberMoreOrLessThanYesterdayCount,
        boolean appointmentNumberGreaterThanYesterday,

        long completedConsultationsTodayCount,
        long consultationsRemainingCount,

        int numberOfActivePatients,
        int numberOfNewPatientThisWeek,

        int pendingTasks,
        int highPriorityTasks,

        Set<DashboardAppointment> todayAppointments,
        Set<RecentUpdate> recentUpdates

)
implements DashboardResponse {


    public record RecentUpdate(String timestamp, String message){}
    public record DashboardAppointment(String appointmentId, String fullName, String dateTime, String age, String channel, boolean isFollowUp) {}
}
