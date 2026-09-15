package com.divjazz.recommendic.dashboard.controller.payload;

import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record ConsultantDashboardResponse(
        long yesterdayTodayConsultationCountDifference,

        long completedConsultationsTodayCount,

        int numberOfActivePatients,
        int numberOfNewPatientThisWeek,

        int pendingTasks,
        int highPriorityTasks,
        List<DashboardConsultation> todayConsultations,
        Set<RecentUpdate> recentUpdates

)
implements DashboardResponse {


    public record RecentUpdate(String timestamp, String message){}

    public record DashboardConsultation(
            String appointmentId,
            String fullName,
            String dateTime,
            String age,
            ConsultationChannel channel,
            boolean isFollowUp,
            String reason,
            ConsultationStatus status,
            Boolean isStarted,
            Boolean isEnded,
            String zoneOffset
    ) {}
}
