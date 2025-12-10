package com.divjazz.recommendic.dashboard.service;

import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.dashboard.controller.payload.ConsultantDashboardResponse;
import com.divjazz.recommendic.dashboard.controller.payload.DashboardResponse;
import com.divjazz.recommendic.dashboard.controller.payload.PatientDashboardResponse;
import com.divjazz.recommendic.notification.app.service.AppNotificationService;
import com.divjazz.recommendic.security.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {


    private final AuthUtils authUtils;
    private final AppointmentService appointmentService;
    private final ConsultationService consultationService;
    private final AppNotificationService appNotificationService;

    @Transactional
    public DashboardResponse getUserDashBoard() {
        var currentUser = authUtils.getCurrentUser();

        return switch (currentUser.userType()) {
            case PATIENT -> getPatientDashboardInfo();
            case ADMIN -> null;
            case CONSULTANT -> getConsultantDashboardInfo(currentUser.userId());
        };
    }

    public PatientDashboardResponse getPatientDashboardInfo() {
        return new PatientDashboardResponse();
    }

    public ConsultantDashboardResponse getConsultantDashboardInfo(String consultantId) {
        var todayAppointments = appointmentService.getTodayAppointmentByConsultantId(consultantId);
        var countOfYesterdayAppointment = appointmentService.getCountAppointmentByConsultantIdAndDate(
                consultantId, LocalDate.now().minusDays(1)
        );
        var countOfCompletedAppointment = consultationService.countOfCompletedConsultationsFromAppointmentIds(
                todayAppointments.stream()
                        .map(Appointment::getAppointmentId)
                        .collect(Collectors.toSet())
        );

        Set<ConsultantDashboardResponse.RecentUpdate> recentUpdates = appNotificationService.getLatest5NotificationsForThisUser()
                .stream().map(notificationDTO -> new ConsultantDashboardResponse.RecentUpdate(
                        notificationDTO.timeStamp().toString(),
                        "%s: %s".formatted(notificationDTO.header(), notificationDTO.summary())
                )).collect(Collectors.toSet());

        return new ConsultantDashboardResponse(
                todayAppointments.size(),
                countOfYesterdayAppointment,
                todayAppointments.size() > countOfYesterdayAppointment,
                countOfCompletedAppointment,
                todayAppointments.size() - countOfCompletedAppointment,
                0,0,0,0, Set.of(),recentUpdates
        );
    }
}
