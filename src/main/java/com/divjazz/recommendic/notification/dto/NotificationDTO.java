package com.divjazz.recommendic.notification.dto;

public record NotificationDTO(String header, String summary, String userId, boolean seen) {
}
