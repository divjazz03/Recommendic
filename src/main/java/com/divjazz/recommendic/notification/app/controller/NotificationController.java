package com.divjazz.recommendic.notification.app.controller;

import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.global.general.Cursor;
import com.divjazz.recommendic.global.general.CursorPageResponse;
import com.divjazz.recommendic.notification.app.controller.payload.NotificationResponse;
import com.divjazz.recommendic.notification.app.controller.payload.NotificationSettingResponse;
import com.divjazz.recommendic.notification.app.controller.payload.NotificationSettingUpdateRequest;
import com.divjazz.recommendic.notification.app.service.AppNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final AppNotificationService appNotificationService;

    @GetMapping
    public Response<CursorPageResponse<NotificationResponse>> getNotifications(
            @RequestParam(value = "limit", defaultValue = "20") String limit,
            @RequestParam(value = "cursorCreatedAt", required = false) String cursorCreatedAt,
            @RequestParam(value = "cursorId", required = false) String cursorId
    ) {
        Cursor cursor = Objects.isNull(cursorId) || Objects.isNull(cursorCreatedAt) ? null
                : Cursor.decode(cursorCreatedAt,cursorId);
        List<NotificationResponse> notifications = appNotificationService.getNotificationsForAuthenticatedUser(cursor,Integer.parseInt(limit));

        return getResponse(CursorPageResponse.from(notifications, Integer.parseInt(limit)),  HttpStatus.OK);
    }
    @PatchMapping("/read")
    public ResponseEntity<Void> seeNotification(@RequestBody String notificationId) {
        appNotificationService.setNotificationToSeen(notificationId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/read/all")
    public ResponseEntity<Void> seeAllNotification() {
        appNotificationService.setAllNotificationToSeen();
        return ResponseEntity.ok().build();
    }
}
