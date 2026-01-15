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
        Long cursorId
) implements Cursorable {
    @Override
    public Instant cursorCreatedAt() {
        return null;
    }

    @Override
    public Long cursorId() {
        return 0L;
    }
}
