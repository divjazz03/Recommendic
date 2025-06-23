package com.divjazz.recommendic.notification.dto;

import com.divjazz.recommendic.notification.enums.NotificationCategory;

public record NotificationDTO(String header, String summary, String userId, boolean seen, NotificationCategory category) {
}
