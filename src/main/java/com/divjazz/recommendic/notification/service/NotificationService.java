package com.divjazz.recommendic.notification.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.notification.dto.NotificationDTO;
import com.divjazz.recommendic.notification.enums.NotificationCategory;
import com.divjazz.recommendic.notification.model.Notification;
import com.divjazz.recommendic.notification.repository.NotificationRepository;
import com.divjazz.recommendic.security.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AuthUtils authUtils;


    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        Notification notification = new Notification(notificationDTO.header(),
                notificationDTO.summary(),
                notificationDTO.targetId(),
                false,
                notificationDTO.category(),
                String.valueOf(notificationDTO.subjectId()));
        notificationRepository.save(notification);
        return notificationDTO;
    }
    @Transactional
    public NotificationDTO setNotificationToSeen(Long notificationId) {
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification with id: %s not found".formatted(notificationId)));
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
                .findAllByForUserId(authUtils.getCurrentUser().getUserId(), pageable)
                .map(notification -> new NotificationDTO(notification.getHeader(),
                        notification.getSummary(),
                        notification.getForUserId(),
                        notification.getCategory() == NotificationCategory.USER ? notification.getSubjectId():Long.parseLong(notification.getSubjectId()),
                        notification.isSeen(),
                        notification.getCategory())
                ));
    }
}
