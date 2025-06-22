package com.divjazz.recommendic.notification.service;

import com.divjazz.recommendic.general.PageResponse;
import com.divjazz.recommendic.notification.dto.NotificationDTO;
import com.divjazz.recommendic.notification.model.Notification;
import com.divjazz.recommendic.notification.repository.NotificationRepository;
import com.divjazz.recommendic.security.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AuthUtils authUtils;


    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        Notification notification = new Notification(notificationDTO.header(),
                notificationDTO.summary(),
                notificationDTO.userId(),
                false);
        notificationRepository.save(notification);
        return notificationDTO;
    }

    public PageResponse<NotificationDTO> getNotificationsForAuthenticatedUser(Pageable pageable) {
        return PageResponse.from(notificationRepository
                .findAllByUserId(authUtils.getCurrentUser().getUserId(), pageable)
                .map(notification -> new NotificationDTO(notification.getHeader(),
                        notification.getSummary(),
                        notification.getUserId(),
                        notification.isSeen())
                ));
    }
}
