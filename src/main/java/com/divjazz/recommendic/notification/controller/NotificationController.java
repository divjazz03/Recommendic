package com.divjazz.recommendic.notification.controller;

import com.divjazz.recommendic.RequestUtils;
import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.general.PageResponse;
import com.divjazz.recommendic.notification.dto.NotificationDTO;
import com.divjazz.recommendic.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Response<PageResponse<NotificationDTO>>> getNotifications(
            @PageableDefault Pageable pageable
    ) {
        PageResponse<NotificationDTO> notifications = notificationService.getNotificationsForAuthenticatedUser(pageable);

        return ResponseEntity.ok(RequestUtils.getResponse(notifications, "success", HttpStatus.OK));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Void> seeNotification(@PathVariable("id") Long notificationId) {
        notificationService.setNotificationToSeen(notificationId);
        return ResponseEntity.ok().build();
    }
}
