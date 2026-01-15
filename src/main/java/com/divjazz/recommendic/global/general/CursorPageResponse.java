package com.divjazz.recommendic.global.general;

import java.util.Base64;
import java.util.List;

public record CursorPageResponse <T>(
        List<T> data,
        String nextCursor
) {

    public static <T extends Cursorable> CursorPageResponse<T> from(List<T> items) {
        if (items.isEmpty()) {
            return new CursorPageResponse<>(items, null);
        }
        T last = items.getLast();

        String rawCursor = "%s:%s".formatted(last.cursorCreatedAt().toEpochMilli() , last.cursorId());
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(rawCursor.getBytes());


        return new CursorPageResponse<>(items, encoded);

    }
}
