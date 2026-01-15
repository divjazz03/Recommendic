package com.divjazz.recommendic.notification.app.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.Cursor;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.notification.app.controller.payload.*;
import com.divjazz.recommendic.notification.app.dto.NotificationDTO;
import com.divjazz.recommendic.notification.app.model.AppNotification;
import com.divjazz.recommendic.notification.app.model.ConsultantNotificationSetting;
import com.divjazz.recommendic.notification.app.model.PatientNotificationSetting;
import com.divjazz.recommendic.notification.app.repository.ConsultantNotificationSettingRepository;
import com.divjazz.recommendic.notification.app.repository.NotificationRepository;
import com.divjazz.recommendic.notification.app.repository.PatientNotificationSettingRepository;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppNotificationService {

    private final NotificationRepository notificationRepository;
    private final AuthUtils authUtils;
    private final PatientNotificationSettingRepository patientNotificationSettingRepository;
    private final ConsultantNotificationSettingRepository consultantNotificationSettingRepository;


    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        AppNotification appNotification = AppNotification.builder()
                .category(notificationDTO.category())
                .subjectId(notificationDTO.subjectId())
                .forUserId(notificationDTO.subjectId())
                .header(notificationDTO.header())
                .seen(false)
                .summary(notificationDTO.summary())
                .build();
        notificationRepository.save(appNotification);
        return notificationDTO;
    }


    public void createNotificationSetting(User user) {
        switch (user.getUserType()) {
            case CONSULTANT -> {
                Consultant consultant = (Consultant) user;
                var consultantNotificationSetting = new ConsultantNotificationSetting();
                consultantNotificationSetting.setConsultant(consultant);
                consultantNotificationSettingRepository.save(consultantNotificationSetting);
            }
            case PATIENT -> {
                Patient patient = (Patient) user;
                var patientNotificationSetting = new PatientNotificationSetting();
                patientNotificationSetting.setPatient(patient);
                patientNotificationSettingRepository.save(patientNotificationSetting);
            }
            default -> {
            }
        }
    }

    public Set<NotificationDTO> getLatest5NotificationsForThisUser() {
        var currentUser = authUtils.getCurrentUser();
        return notificationRepository.findTop5ByForUserIdOrderByNotificationIdDesc(currentUser.userId())
                .stream()
                .map(appNotification -> new NotificationDTO(
                        appNotification.getHeader(),
                        appNotification.getSummary(),
                        appNotification.getSubjectId(),
                        appNotification.getForUserId(),
                        appNotification.isSeen(),
                        appNotification.getCategory(),
                        appNotification.getCreatedAt()
                )).collect(Collectors.toSet());
    }


    public void setNotificationToSeen(String notificationId) {
        notificationRepository.setNotificationToSeenById(notificationId);
    }

    public void setAllNotificationToSeen() {
        var currentUser = authUtils.getCurrentUser();
        notificationRepository.setAllNotificationToSeenForUserId(currentUser.userId());
    }

    public List<NotificationResponse> getNotificationsForAuthenticatedUser(Cursor cursor, Pageable pageable) {
        if (cursor == null) {
            return notificationRepository.findNextPage(
                            authUtils.getCurrentUser().userId(), null, null, pageable)
                    .stream().map(notification ->
                            new NotificationResponse(
                                    notification.getNotificationId(),
                                    notification.getCategory().toString(),
                                    notification.getSubjectId(),
                                    notification.getSummary(),
                                    notification.isSeen(),
                                    notification.getHeader(),
                                    notification.getCreatedAt().toString(),
                                    notification.getCreatedAt(),
                                    notification.getId()

                            )).toList();
        }
        return notificationRepository.findNextPage(
                authUtils.getCurrentUser().userId(),
                cursor.createdAt(),
                cursor.id(),
                pageable
        ).stream().map(notification ->
                new NotificationResponse(
                        notification.getNotificationId(),
                        notification.getCategory().toString(),
                        notification.getSubjectId(),
                        notification.getSummary(),
                        notification.isSeen(),
                        notification.getHeader(),
                        notification.getCreatedAt().toString(),
                        notification.getCreatedAt(),
                        notification.getId()

                )).toList();
    }

    public NotificationSettingResponse getNotificationSettingConfiguration() {
        var user = authUtils.getCurrentUser();
        return switch (authUtils.getCurrentUser().userType()) {
            case PATIENT -> {
                var notificationSetting = patientNotificationSettingRepository
                        .findByPatient_UserId(user.userId())
                        .orElseThrow(() -> new EntityNotFoundException("Notification Setting for user %s is not found"
                                .formatted(user.userId())));
                yield toPatientNotificationResponse(notificationSetting);
            }
            case CONSULTANT -> {
                var notificationSetting = consultantNotificationSettingRepository
                        .findByConsultant_UserId(user.userId())
                        .orElseThrow(() -> new EntityNotFoundException("Notification Setting for user %s is not found"
                                .formatted(user.userId())));
                yield toConsultantNotificationResponse(notificationSetting);
            }
            case null, default -> null;
        };
    }

    public NotificationSettingResponse updateNotificationSettingConfiguration(NotificationSettingUpdateRequest updateRequest) {
        var user = authUtils.getCurrentUser();
        return switch (user.userType()) {
            case CONSULTANT -> {
                var notificationSetting = consultantNotificationSettingRepository.findByConsultant_UserId(user.userId())
                        .orElseThrow(() -> new EntityNotFoundException("Notification setting not found for this use"));
                if (Objects.isNull(updateRequest)) {
                    yield toConsultantNotificationResponse(notificationSetting);
                }
                var notificationRequest = (ConsultantNotificationSettingUpdateRequest) updateRequest;
                if (Objects.nonNull(notificationRequest.emailNotificationsEnabled())
                        && Boolean.compare(notificationRequest.emailNotificationsEnabled(),
                        notificationSetting.isEmailNotificationEnabled()) != 0) {
                    notificationSetting.setEmailNotificationEnabled(notificationRequest.emailNotificationsEnabled());
                }
                if (Objects.nonNull(notificationRequest.appointmentRemindersEnabled())
                        && Boolean.compare(notificationRequest.appointmentRemindersEnabled(),
                        notificationSetting.isAppointmentRemindersEnabled()) != 0) {
                    notificationSetting.setAppointmentRemindersEnabled(notificationRequest.appointmentRemindersEnabled());
                }
                if (Objects.nonNull(notificationRequest.labResultUpdatesEnabled())
                        && Boolean.compare(notificationRequest.labResultUpdatesEnabled(),
                        notificationSetting.isLabResultsUpdateEnabled()) != 0) {
                    notificationSetting.setLabResultsUpdateEnabled(notificationRequest.labResultUpdatesEnabled());
                }
                if (Objects.nonNull(notificationRequest.marketingEmailsEnabled())
                        && Boolean.compare(notificationRequest.marketingEmailsEnabled(),
                        notificationSetting.isMarketingEmailEnabled()) != 0) {
                    notificationSetting.setMarketingEmailEnabled(notificationRequest.marketingEmailsEnabled());
                }
                if (Objects.nonNull(notificationRequest.smsNotificationsEnabled())
                        && Boolean.compare(notificationRequest.smsNotificationsEnabled(),
                        notificationSetting.isSmsNotificationEnabled()) != 0) {
                    notificationSetting.setSmsNotificationEnabled(notificationRequest.smsNotificationsEnabled());
                }
                if (Objects.nonNull(notificationRequest.systemUpdatesEnabled())
                        && Boolean.compare(notificationRequest.systemUpdatesEnabled(),
                        notificationSetting.isSystemUpdatesEnabled()) != 0) {
                    notificationSetting.setSystemUpdatesEnabled(notificationRequest.systemUpdatesEnabled());
                }

                notificationSetting = consultantNotificationSettingRepository.save(notificationSetting);
                yield toConsultantNotificationResponse(notificationSetting);

            }
            case PATIENT -> {
                var notificationSetting = patientNotificationSettingRepository.findByPatient_UserId(user.userId())
                        .orElseThrow(() -> new EntityNotFoundException("Notification setting not found for this use"));
                if (Objects.isNull(updateRequest)) {
                    yield toPatientNotificationResponse(notificationSetting);
                }
                var notificationRequest = (PatientNotificationSettingUpdateRequest) updateRequest;
                if (Objects.nonNull(notificationRequest.emailNotificationsEnabled())
                        && Boolean.compare(notificationRequest.emailNotificationsEnabled(),
                        notificationSetting.isEmailNotificationEnabled()) != 0) {
                    notificationSetting.setEmailNotificationEnabled(notificationRequest.emailNotificationsEnabled());
                }
                if (Objects.nonNull(notificationRequest.appointmentRemindersEnabled())
                        && Boolean.compare(notificationRequest.appointmentRemindersEnabled(),
                        notificationSetting.isAppointmentRemindersEnabled()) != 0) {
                    notificationSetting.setAppointmentRemindersEnabled(notificationRequest.appointmentRemindersEnabled());
                }
                if (Objects.nonNull(notificationRequest.labResultUpdatesEnabled())
                        && Boolean.compare(notificationRequest.labResultUpdatesEnabled(),
                        notificationSetting.isLabResultsUpdateEnabled()) != 0) {
                    notificationSetting.setLabResultsUpdateEnabled(notificationRequest.labResultUpdatesEnabled());
                }
                if (Objects.nonNull(notificationRequest.marketingEmailsEnabled())
                        && Boolean.compare(notificationRequest.marketingEmailsEnabled(),
                        notificationSetting.isMarketingEmailEnabled()) != 0) {
                    notificationSetting.setMarketingEmailEnabled(notificationRequest.marketingEmailsEnabled());
                }
                if (Objects.nonNull(notificationRequest.smsNotificationsEnabled())
                        && Boolean.compare(notificationRequest.smsNotificationsEnabled(),
                        notificationSetting.isSmsNotificationEnabled()) != 0) {
                    notificationSetting.setSmsNotificationEnabled(notificationRequest.smsNotificationsEnabled());
                }
                if (Objects.nonNull(notificationRequest.systemUpdatesEnabled())
                        && Boolean.compare(notificationRequest.systemUpdatesEnabled(),
                        notificationSetting.isSystemUpdatesEnabled()) != 0) {
                    notificationSetting.setSystemUpdatesEnabled(notificationRequest.systemUpdatesEnabled());
                }

                notificationSetting = patientNotificationSettingRepository.save(notificationSetting);
                yield toPatientNotificationResponse(notificationSetting);
            }
            case null, default -> throw new IllegalStateException("invalid user type");
        };
    }

    private PatientNotificationSettingResponse toPatientNotificationResponse(PatientNotificationSetting notificationSetting) {
        return new PatientNotificationSettingResponse(
                notificationSetting.isEmailNotificationEnabled(),
                notificationSetting.isSmsNotificationEnabled(),
                notificationSetting.isAppointmentRemindersEnabled(),
                notificationSetting.isLabResultsUpdateEnabled(),
                notificationSetting.isSystemUpdatesEnabled(),
                notificationSetting.isMarketingEmailEnabled()
        );
    }

    private ConsultantNotificationSettingResponse toConsultantNotificationResponse(ConsultantNotificationSetting notificationSetting) {
        return new ConsultantNotificationSettingResponse(
                notificationSetting.isEmailNotificationEnabled(),
                notificationSetting.isSmsNotificationEnabled(),
                notificationSetting.isAppointmentRemindersEnabled(),
                notificationSetting.isLabResultsUpdateEnabled(),
                notificationSetting.isSystemUpdatesEnabled(),
                notificationSetting.isMarketingEmailEnabled()
        );
    }
}
