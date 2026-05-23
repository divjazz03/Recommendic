package com.divjazz.recommendic.notification.app.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.Cursor;
import com.divjazz.recommendic.notification.app.controller.payload.*;
import com.divjazz.recommendic.notification.app.dto.NotificationDTO;
import com.divjazz.recommendic.notification.app.model.AppNotification;
import com.divjazz.recommendic.notification.app.repository.NotificationRepository;
import com.divjazz.recommendic.security.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<NotificationResponse> getNotificationsForAuthenticatedUser(Cursor cursor, Integer limit) {
        if (cursor == null) {
            return new ArrayList<>(notificationRepository.findNextPage(
                            authUtils.getCurrentUser().userId(), null, null, limit)
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
                                    notification.getNotificationId()

                            )).toList());
        }
        return new ArrayList<>(notificationRepository.findNextPage(
                authUtils.getCurrentUser().userId(),
                cursor.createdAt(),
                cursor.id(),
                limit
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
                        notification.getNotificationId()

                )).toList());
    }

}
