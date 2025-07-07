package com.divjazz.recommendic.article.dto;

public record ArticleSearchDTO(
        long id,
        String title,
        String subtitle,
        String authorFirstName,
        String authorLastName,
        String publishedAt,
        String[] tags,
        double rank,
        String highlighted,
        long upvotes,
        long numberOfComment,
        long reads,
        long total
) {
}
