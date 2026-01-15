package com.divjazz.recommendic.notification.app.controller;

import com.divjazz.recommendic.global.RequestUtils;
import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.global.general.Cursor;
import com.divjazz.recommendic.global.general.CursorPageResponse;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.notification.app.controller.payload.ConsultantNotificationSettingUpdateRequest;
import com.divjazz.recommendic.notification.app.controller.payload.NotificationResponse;
import com.divjazz.recommendic.notification.app.controller.payload.NotificationSettingResponse;
import com.divjazz.recommendic.notification.app.controller.payload.NotificationSettingUpdateRequest;
import com.divjazz.recommendic.notification.app.dto.NotificationDTO;
import com.divjazz.recommendic.notification.app.service.AppNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final AppNotificationService appNotificationService;

    @GetMapping
    public ResponseEntity<Response<CursorPageResponse<NotificationResponse>>> getNotifications(
            @PageableDefault Pageable pageable,
            @RequestParam("pageParam") String pageParam
    ) {
        Cursor cursor = (Objects.isNull(pageParam))
                ? null
                : Cursor.fromPageParam(pageParam);
        List<NotificationResponse> notifications = appNotificationService.getNotificationsForAuthenticatedUser(cursor,pageable);

        return ResponseEntity.ok(getResponse(CursorPageResponse.from(notifications),  HttpStatus.OK));
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

    @GetMapping("/settings")
    public ResponseEntity<Response<NotificationSettingResponse>> getNotificationSettings() {
        var response = appNotificationService.getNotificationSettingConfiguration();

        return ResponseEntity.ok(getResponse(response,HttpStatus.OK));
    }
    @PatchMapping("/settings")
    public ResponseEntity<Response<NotificationSettingResponse>> updateNotificationSettings(@RequestBody NotificationSettingUpdateRequest updateRequest) {
        var response = appNotificationService.updateNotificationSettingConfiguration(updateRequest);

        return ResponseEntity.ok(getResponse(response, HttpStatus.OK));
    }

}
