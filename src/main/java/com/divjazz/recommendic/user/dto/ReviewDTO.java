package com.divjazz.recommendic.user.dto;

import java.time.OffsetDateTime;

public record ReviewDTO(
        String name,
        int rating,
        String comment,
        OffsetDateTime date
) {
}
