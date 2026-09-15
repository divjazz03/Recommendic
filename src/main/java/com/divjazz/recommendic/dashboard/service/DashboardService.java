package com.divjazz.recommendic.dashboard.service;

import com.divjazz.recommendic.appointment.enums.AppointmentHistory;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.repository.projection.AppointmentProjection;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.repository.projection.ConsultationProjection;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.dashboard.controller.payload.ConsultantDashboardResponse;
import com.divjazz.recommendic.dashboard.controller.payload.DashboardResponse;
import com.divjazz.recommendic.dashboard.controller.payload.PatientDashboardResponse;
import com.divjazz.recommendic.global.general.DateUtils;
import com.divjazz.recommendic.notification.app.service.AppNotificationService;
import com.divjazz.recommendic.security.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {


    private final AuthUtils authUtils;
    private final ConsultationService consultationService;
    private final AppNotificationService appNotificationService;

    @Transactional
    public DashboardResponse getUserDashBoard() {
        var currentUser = authUtils.getCurrentUser();

        return switch (currentUser.userType()) {
            case PATIENT -> getPatientDashboardInfo(currentUser.userId());
            case ADMIN -> null;
            case CONSULTANT -> getConsultantDashboardInfo(currentUser.userId());
        };
    }

    public PatientDashboardResponse getPatientDashboardInfo(String patientId) {

        var todayConsultations = consultationService.retrieveConsultationsByPatientIdToday(patientId);

        List<PatientDashboardResponse.DashboardConsultation> consultations = todayConsultations
                .stream()
                .map(consultation -> new PatientDashboardResponse.DashboardConsultation(
                        consultation.getConsultationId(),
                        consultation.getPatientFullName().getFullName(),
                        consultation.getConsultantFullName().getFullName(),
                        "%sT%s".formatted(consultation.getStartDate(),consultation.getStartTime().toString()),
                        consultation.getChannel(),
                        consultation.getStatus(),
                        consultation.getStartedAt() != null,
                        consultation.getEndedAt() != null,
                        consultation.getOffset().getId())
                ).toList();
        Set<PatientDashboardResponse.RecentActivity> recentActivities = appNotificationService.getLatest5NotificationsForThisUser()
                .stream().map(notificationDTO -> new PatientDashboardResponse.RecentActivity(
                        notificationDTO.header(),
                        notificationDTO.timeStamp().toString(),
                        notificationDTO.category()
                )).collect(Collectors.toSet());
        Set<PatientDashboardResponse.Medication> medications = Set.of();
        return new PatientDashboardResponse(consultations,recentActivities,medications);
    }

    public ConsultantDashboardResponse getConsultantDashboardInfo(String consultantId) {
        var todayConsultations = consultationService.retrieveConsultationsByConsultantIdToday(consultantId);
        var countOfYesterdayConsultations = consultationService.getCountConsultationsByConsultantIdAndDate(
                consultantId, LocalDate.now().minusDays(1)
        );
        var countOfCompletedConsultations = consultationService.countOfCompletedConsultationsFromAppointmentIds(
                todayConsultations.stream()
                        .map(ConsultationProjection::getConsultantId)
                        .collect(Collectors.toSet())
        );

        Set<ConsultantDashboardResponse.RecentUpdate> recentUpdates = appNotificationService.getLatest5NotificationsForThisUser()
                .stream().map(notificationDTO -> new ConsultantDashboardResponse.RecentUpdate(
                        notificationDTO.timeStamp().toString(),
                        "%s: %s".formatted(notificationDTO.header(), notificationDTO.summary())
                )).collect(Collectors.toSet());
        List<ConsultantDashboardResponse.DashboardConsultation> consultations = todayConsultations.stream()
                .map(consultation -> new ConsultantDashboardResponse.DashboardConsultation(
                        consultation.getConsultationId(),
                        consultation.getPatientFullName().getFullName(),
                        "%sT%s".formatted(consultation.getStartDate(),consultation.getStartTime().toString())
                        ,
                                DateUtils.toAge(consultation.getPatientDateOfBirth()),
                        consultation.getChannel(),
                        consultation.getAppointmentHistory().equals(AppointmentHistory.FOLLOW_UP),
                        consultation.getReason(),
                        consultation.getStatus(),
                        consultation.getStartedAt() != null,
                        consultation.getEndedAt() != null,
                        consultation.getOffset().getId()
                )).toList();

        return ConsultantDashboardResponse.builder()
                .todayAppointments(List.of())
                .todayConsultations(consultations)
                .highPriorityTasks(0)
                .numberOfActivePatients(0)
                .numberOfNewPatientThisWeek(0)
                .recentUpdates(Set.of())
                .completedConsultationsTodayCount(countOfCompletedConsultations)
                .yesterdayTodayAppointmentCountDifference(todayConsultations.size() - countOfYesterdayConsultations)
                .build();
    }
}
