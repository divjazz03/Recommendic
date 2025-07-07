package com.divjazz.recommendic.article.dto;

public record ArticleSearchResponse(
        long id,
        String title,
        String subtitle,
        String authorFirstName,
        String authorLastName,
        long upvote,
        long noOfComments,
        long noOfReads,
        String publishedAt,
        double rank,
        String highlightedText,
        String[] tags
) {
}
