package com.divjazz.recommendic.article.mapper;

import com.divjazz.recommendic.article.dto.ArticleDTO;
import com.divjazz.recommendic.article.model.Article;

public class ArticleMapper {

    public static ArticleDTO articleToArticleDTO(Article article) {

        return new ArticleDTO(
                article.getTitle(),
                article.getSubtitle(),
                article.getContent(),
                null,//article.getTags(),
                0L, //article.getLikeUserIds().length,
                "",
                article.getNumberOfReads(),
                article.getPublished_at().toString()
        );
    }
}
