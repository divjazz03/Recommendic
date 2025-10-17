package com.divjazz.recommendic.notification.app.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.notification.app.dto.NotificationDTO;
import com.divjazz.recommendic.notification.app.enums.NotificationCategory;
import com.divjazz.recommendic.notification.app.model.AppNotification;
import com.divjazz.recommendic.notification.app.repository.NotificationRepository;
import com.divjazz.recommendic.security.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppNotificationService {

    private final NotificationRepository notificationRepository;
    private final AuthUtils authUtils;


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
}
