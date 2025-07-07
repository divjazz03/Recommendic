package com.divjazz.recommendic.article.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ArticleUpload(
        @Size(min = 10, max = 30,
                message = "Title should not be less that 10 and should not exceed 30 characters")
        @NotNull(message = "Title is required")
        String title,
        @Size(min = 10, max = 50,
                message = "Title should not be less that 10 and should not exceed 30 characters")
        @NotNull(message = "Subtitle is required")
        String subtitle,
        @Size(min = 1, max = 5, message = "You must select between 1 to 5 tags")
        String[] tags,
        @Size(min = 100, max = 4000,
                message = "Title should not be less that 10 and should not exceed 30 characters")
        @NotNull(message = "Content is required")
        String content
) {
}
