package com.divjazz.recommendic.notification.app.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.notification.app.controller.payload.*;
import com.divjazz.recommendic.notification.app.dto.NotificationDTO;
import com.divjazz.recommendic.notification.app.enums.NotificationCategory;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

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

    @Async("recommendicTaskExecutor")
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
            case null, default -> {
            }
        }
    }

    @Transactional
    public NotificationDTO setNotificationToSeen(Long notificationId) {
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("AppNotification with id: %s not found".formatted(notificationId)));
        notification.setSeen(true);

        return new NotificationDTO(
                notification.getHeader(),
                notification.getSummary(),
                notification.getForUserId(),
                notification.getSubjectId(),
                notification.isSeen(),
                notification.getCategory()
        );
    }

    public PageResponse<NotificationDTO> getNotificationsForAuthenticatedUser(Pageable pageable) {
        return PageResponse.from(notificationRepository
                .findAllByForUserId(authUtils.getCurrentUser().userId(), pageable)
                .map(appNotification -> new NotificationDTO(appNotification.getHeader(),
                        appNotification.getSummary(),
                        appNotification.getForUserId(),
                        appNotification.getSubjectId(),
                        appNotification.isSeen(),
                        appNotification.getCategory())
                ));
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
                if (Objects.nonNull(notificationRequest.emailNotificationEnabled())
                        && Boolean.compare(notificationRequest.emailNotificationEnabled(),
                        notificationSetting.isEmailNotificationEnabled()) != 0) {
                    notificationSetting.setEmailNotificationEnabled(notificationRequest.emailNotificationEnabled());
                }
                if (Objects.nonNull(notificationRequest.appointmentRemindersEnabled())
                        && Boolean.compare(notificationRequest.appointmentRemindersEnabled(),
                        notificationSetting.isAppointmentRemindersEnabled()) != 0) {
                    notificationSetting.setAppointmentRemindersEnabled(notificationRequest.appointmentRemindersEnabled());
                }
                if (Objects.nonNull(notificationRequest.labResultsUpdateEnabled())
                        && Boolean.compare(notificationRequest.labResultsUpdateEnabled(),
                        notificationSetting.isLabResultsUpdateEnabled()) != 0) {
                    notificationSetting.setLabResultsUpdateEnabled(notificationRequest.labResultsUpdateEnabled());
                }
                if (Objects.nonNull(notificationRequest.marketingEmailEnabled())
                        && Boolean.compare(notificationRequest.marketingEmailEnabled(),
                        notificationSetting.isMarketingEmailEnabled()) != 0) {
                    notificationSetting.setMarketingEmailEnabled(notificationRequest.marketingEmailEnabled());
                }
                if (Objects.nonNull(notificationRequest.smsNotificationEnabled())
                        && Boolean.compare(notificationRequest.smsNotificationEnabled(),
                        notificationSetting.isSmsNotificationEnabled()) != 0) {
                    notificationSetting.setSmsNotificationEnabled(notificationRequest.smsNotificationEnabled());
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
                if (Objects.nonNull(notificationRequest.emailNotificationEnabled())
                        && Boolean.compare(notificationRequest.emailNotificationEnabled(),
                        notificationSetting.isEmailNotificationEnabled()) != 0) {
                    notificationSetting.setEmailNotificationEnabled(notificationRequest.emailNotificationEnabled());
                }
                if (Objects.nonNull(notificationRequest.appointmentRemindersEnabled())
                        && Boolean.compare(notificationRequest.appointmentRemindersEnabled(),
                        notificationSetting.isAppointmentRemindersEnabled()) != 0) {
                    notificationSetting.setAppointmentRemindersEnabled(notificationRequest.appointmentRemindersEnabled());
                }
                if (Objects.nonNull(notificationRequest.labResultsUpdateEnabled())
                        && Boolean.compare(notificationRequest.labResultsUpdateEnabled(),
                        notificationSetting.isLabResultsUpdateEnabled()) != 0) {
                    notificationSetting.setLabResultsUpdateEnabled(notificationRequest.labResultsUpdateEnabled());
                }
                if (Objects.nonNull(notificationRequest.marketingEmailEnabled())
                        && Boolean.compare(notificationRequest.marketingEmailEnabled(),
                        notificationSetting.isMarketingEmailEnabled()) != 0) {
                    notificationSetting.setMarketingEmailEnabled(notificationRequest.marketingEmailEnabled());
                }
                if (Objects.nonNull(notificationRequest.smsNotificationEnabled())
                        && Boolean.compare(notificationRequest.smsNotificationEnabled(),
                        notificationSetting.isSmsNotificationEnabled()) != 0) {
                    notificationSetting.setSmsNotificationEnabled(notificationRequest.smsNotificationEnabled());
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
