package com.divjazz.recommendic.dashboard.controller.payload;

import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;

import java.util.List;
import java.util.Set;

public record ConsultantDashboardResponse(
        int yesterdayTodayAppointmentCountDifference,

        long completedConsultationsTodayCount,

        int numberOfActivePatients,
        int numberOfNewPatientThisWeek,

        int pendingTasks,
        int highPriorityTasks,

        List<DashboardAppointment> todayAppointments,
        Set<RecentUpdate> recentUpdates

)
implements DashboardResponse {


    public record RecentUpdate(String timestamp, String message){}
    public record DashboardAppointment(
            String appointmentId,
            String fullName,
            String dateTime,
            String age,
            ConsultationChannel channel,
            boolean isFollowUp,
            String reason,
            AppointmentStatus status,
            Boolean isStarted,
            Boolean isEnded
    ) {}
}
