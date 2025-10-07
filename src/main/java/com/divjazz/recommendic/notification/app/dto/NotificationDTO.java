package com.divjazz.recommendic.notification.app.dto;

import com.divjazz.recommendic.notification.app.enums.NotificationCategory;

public record NotificationDTO(String header,
                              String summary,
                              String targetId,
                              String subjectId,
                              boolean seen, NotificationCategory category) {
}
