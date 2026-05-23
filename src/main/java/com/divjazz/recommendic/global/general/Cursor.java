package com.divjazz.recommendic.global.general;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public record Cursor(
        Instant createdAt,
        String id
) {
    public static Cursor decode(String cursorCreatedAt, String cursorId) {
        String decodedCreatedAt = new String(Base64.getUrlDecoder().decode(cursorCreatedAt));
        String decodedCursorId = new String((Base64.getUrlDecoder().decode(cursorId)));
        log.info("Decoded CreatedAt {}", decodedCreatedAt);
        return new Cursor(Instant.parse(decodedCreatedAt), decodedCursorId);
    }
}
