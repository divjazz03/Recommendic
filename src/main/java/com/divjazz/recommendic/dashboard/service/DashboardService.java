package com.divjazz.recommendic.dashboard.service;

import com.divjazz.recommendic.appointment.enums.AppointmentHistory;
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
            case PATIENT -> getPatientDashboardInfo(currentUser.userId());
            case ADMIN -> null;
            case CONSULTANT -> getConsultantDashboardInfo(currentUser.userId());
        };
    }

    public PatientDashboardResponse getPatientDashboardInfo(String patientId) {

        var todayAppointments = appointmentService.getTodayAppointmentByPatientId(patientId);

        Set<PatientDashboardResponse.DashboardAppointment> appointments = todayAppointments
                .stream()
                .map(appointment -> new PatientDashboardResponse.DashboardAppointment(
                        appointment.getAppointmentId(),
                        appointment.getConsultant().getProfile().getUserName().getFullName(),
                        appointment.getConsultant().getSpecialization().getName(),
                        appointment.getStartDateAndTime().toString(),
                        appointment.getConsultationChannel()
                ))
                .collect(Collectors.toSet());
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
                        .map(Appointment::getAppointmentId)
                        .collect(Collectors.toSet())
        );

        Set<ConsultantDashboardResponse.RecentUpdate> recentUpdates = appNotificationService.getLatest5NotificationsForThisUser()
                .stream().map(notificationDTO -> new ConsultantDashboardResponse.RecentUpdate(
                        notificationDTO.timeStamp().toString(),
                        "%s: %s".formatted(notificationDTO.header(), notificationDTO.summary())
                )).collect(Collectors.toSet());
        Set<ConsultantDashboardResponse.DashboardAppointment> appointments = todayAppointments.stream()
                .map(appointment -> new ConsultantDashboardResponse.DashboardAppointment(
                        appointment.getAppointmentId(),
                        appointment.getPatient().getPatientProfile().getUserName().getFullName(),
                        appointment.getStartDateAndTime().toString(),
                        appointment.getPatient().getPatientProfile().getAge(),
                        appointment.getConsultationChannel(),
                        appointment.getHistory().equals(AppointmentHistory.FOLLOW_UP),
                        appointment.getReason()
                ))
                .collect(Collectors.toSet());

        return new ConsultantDashboardResponse(
               todayAppointments.size() - countOfYesterdayAppointment,
                countOfCompletedAppointment,
                0,0,0,0,appointments,recentUpdates
        );
    }
}
