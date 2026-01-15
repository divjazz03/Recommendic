package com.divjazz.recommendic.global.general;

import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;

public record Cursor(
        Instant createdAt,
        Long id
) {
    public static Cursor fromPageParam(String param) {
        String decodedParam = Arrays.toString(Base64.getUrlDecoder().decode(param));
        String[] splitParam = decodedParam.split(":");
        long createdAtInMillis = Long.parseLong(splitParam[0]);
        long cursorId = Long.parseLong(splitParam[1]);

        return new Cursor(Instant.ofEpochMilli(createdAtInMillis), cursorId);
    }
}
