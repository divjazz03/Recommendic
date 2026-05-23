package com.divjazz.recommendic.global.general;

import java.util.Base64;
import java.util.List;

public record CursorPageResponse <T>(
        List<T> data,
        CursorResponse nextCursor
) {

    public static <T extends Cursorable> CursorPageResponse<T> from(List<T> items, Integer limit) {
        if (items.isEmpty()) {
            return new CursorPageResponse<>(items, null);
        }
        T last = items.getLast();

        String encodedCreatedAt = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(last.cursorCreatedAt().toString().getBytes());
        String encodedCursorId = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(last.cursorId().getBytes());

        var hasNext = items.size() > limit;
        items.removeLast();

        return new CursorPageResponse<>(items, new CursorResponse(encodedCreatedAt, encodedCursorId, hasNext ));

    }
}
