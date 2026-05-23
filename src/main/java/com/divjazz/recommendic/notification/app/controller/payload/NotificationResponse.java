package com.divjazz.recommendic.notification.app.controller.payload;

import com.divjazz.recommendic.global.general.Cursorable;

import java.time.Instant;

public record NotificationResponse(
        String id,
        String type,
        String subjectId,
        String message,
        boolean isRead,
        String title,
        String timeStamp,
        Instant cursorCreatedAt,
        String cursorId
) implements Cursorable {
    @Override
    public Instant cursorCreatedAt() {
        return cursorCreatedAt;
    }

    @Override
    public String cursorId() {
        return id;
    }
}
