package com.divjazz.recommendic.article.dto;

public record ArticleDTO(
        String title,
        String subtitle,
        String content,
        String[] tags,
        long likes,
        String authorFullName,
        long reads,
        String published_at

) {

}
