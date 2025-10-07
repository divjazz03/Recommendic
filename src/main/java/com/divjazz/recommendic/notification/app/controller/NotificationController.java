package com.divjazz.recommendic.notification.app.controller;

import com.divjazz.recommendic.global.RequestUtils;
import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.notification.app.dto.NotificationDTO;
import com.divjazz.recommendic.notification.app.service.AppNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final AppNotificationService appNotificationService;

    @GetMapping
    public ResponseEntity<Response<PageResponse<NotificationDTO>>> getNotifications(
            @PageableDefault Pageable pageable
    ) {
        PageResponse<NotificationDTO> notifications = appNotificationService.getNotificationsForAuthenticatedUser(pageable);

        return ResponseEntity.ok(RequestUtils.getResponse(notifications,  HttpStatus.OK));
    }
    @PostMapping
    public ResponseEntity<Void> seeNotification(@RequestBody Long notificationId) {
        appNotificationService.setNotificationToSeen(notificationId);
        return ResponseEntity.ok().build();
    }
}
