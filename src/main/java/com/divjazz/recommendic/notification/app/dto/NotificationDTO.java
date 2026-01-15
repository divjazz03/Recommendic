package com.divjazz.recommendic.notification.app.dto;

import com.divjazz.recommendic.notification.app.enums.NotificationCategory;

import java.time.Instant;
import java.time.LocalDateTime;

public record NotificationDTO(String header,
                              String summary,
                              String targetId,
                              String subjectId,
                              boolean seen, NotificationCategory category, Instant timeStamp) {
}
