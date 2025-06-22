package com.divjazz.recommendic.article.dto;

import com.divjazz.recommendic.user.model.userAttributes.UserName;

import java.time.LocalDateTime;
import java.util.Set;

public record ArticleSearchDTO(
        long id,
        String title,
        String subtitle,
        String authorFirstName,
        String authorLastName,
        String publishedAt,
        String[] tags,
        float rank,
        String highlighted,
        long upvotes,
        int numberOfComment,
        int reads,
        long total
) {
}
