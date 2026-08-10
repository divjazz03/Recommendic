package com.divjazz.recommendic.dashboard.service;

import com.divjazz.recommendic.appointment.enums.AppointmentHistory;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.repository.projection.AppointmentProjection;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.consultation.model.Consultation;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {


    private final AuthUtils authUtils;
    private final AppointmentService appointmentService;
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

        var todayAppointments = appointmentService.getTodayAppointmentByPatientId(patientId);

        List<PatientDashboardResponse.DashboardAppointment> appointments = todayAppointments
                .stream()
                .map(appointment -> new PatientDashboardResponse.DashboardAppointment(
                        appointment.getAppointmentId(),
                        appointment.getPatientFullName().getFullName(),
                        appointment.getConsultantFullName().getFullName(),
                        "%sT%s".formatted(appointment.getStartDate(),appointment.getStartTime().toString()),
                        appointment.getChannel(),
                        appointment.getStatus(),
                        appointment.getStartedAt() != null,
                        appointment.getEndedAt() != null)
                ).toList();
        Set<PatientDashboardResponse.RecentActivity> recentActivities = appNotificationService.getLatest5NotificationsForThisUser()
                .stream().map(notificationDTO -> new PatientDashboardResponse.RecentActivity(
                        notificationDTO.header(),
                        notificationDTO.timeStamp().toString(),
                        notificationDTO.category()
                )).collect(Collectors.toSet());
        Set<PatientDashboardResponse.Medication> medications = Set.of();
        return new PatientDashboardResponse(appointments,recentActivities,medications);
    }

    public ConsultantDashboardResponse getConsultantDashboardInfo(String consultantId) {
        var todayAppointments = appointmentService.getTodayAppointmentByConsultantId(consultantId);
        var countOfYesterdayAppointment = appointmentService.getCountAppointmentByConsultantIdAndDate(
                consultantId, LocalDate.now().minusDays(1)
        );
        var countOfCompletedAppointment = consultationService.countOfCompletedConsultationsFromAppointmentIds(
                todayAppointments.stream()
                        .map(AppointmentProjection::getAppointmentId)
                        .collect(Collectors.toSet())
        );

        Set<ConsultantDashboardResponse.RecentUpdate> recentUpdates = appNotificationService.getLatest5NotificationsForThisUser()
                .stream().map(notificationDTO -> new ConsultantDashboardResponse.RecentUpdate(
                        notificationDTO.timeStamp().toString(),
                        "%s: %s".formatted(notificationDTO.header(), notificationDTO.summary())
                )).collect(Collectors.toSet());
        List<ConsultantDashboardResponse.DashboardAppointment> appointments = todayAppointments.stream()
                .map(appointment -> new ConsultantDashboardResponse.DashboardAppointment(
                        appointment.getAppointmentId(),
                        appointment.getPatientFullName().getFullName(),
                        "%sT%s".formatted(appointment.getStartDate(),appointment.getStartTime().toString())
                        ,
                                DateUtils.toAge(appointment.getPatientDateOfBirth()),
                        appointment.getChannel(),
                        appointment.getAppointmentHistory().equals(AppointmentHistory.FOLLOW_UP),
                        appointment.getReason(),
                        appointment.getStatus(),
                        appointment.getStartedAt() != null,
                        appointment.getEndedAt() != null
                )).toList();

        return new ConsultantDashboardResponse(
               todayAppointments.size() - countOfYesterdayAppointment,
                countOfCompletedAppointment,
                0,0,0,0,appointments,recentUpdates
        );
    }
}
