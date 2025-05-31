package com.divjazz.recommendic.article.dto;

public record ArticleSearchResponse(
        long id,
        String title,
        String subtitle,
        String authorFirstName,
        String authorLastName,
        long upvote,
        int noOfComments,
        long noOfReads,
        String publishedAt,
        float rank,
        String highlightedText,
        String[] tags
) {
}
