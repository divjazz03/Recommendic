package com.divjazz.recommendic.article.mapper;

import com.divjazz.recommendic.article.dto.ArticleDTO;
import com.divjazz.recommendic.article.model.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ArticleMapper {

    @Mapping(target = "likes", expression = "java(article.getLikeUserIds().length)")
    @Mapping(target = "authorFullName",
            expression = """
                    java(
                        article.getConsultant().getProfile().getTitle()
                        + " "
                        + article.getConsultant()
                                     .getProfile()
                                     .getUserName()
                                     .getFullName()
                    )
                    """)
    @Mapping(target = "reads", source = "numberOfReads")
    ArticleDTO articleToArticleDTO(Article article);
}
