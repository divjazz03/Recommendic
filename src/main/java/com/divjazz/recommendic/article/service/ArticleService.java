package com.divjazz.recommendic.article.service;

import com.divjazz.recommendic.article.dto.ArticleDTO;
import com.divjazz.recommendic.article.dto.ArticleSearchDTO;
import com.divjazz.recommendic.article.dto.ArticleSearchResponse;
import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.article.repository.ArticleRepository;
import com.divjazz.recommendic.general.PageResponse;
import com.divjazz.recommendic.general.Sort;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final AuthUtils authUtils;

    public ArticleService(ArticleRepository articleRepository, AuthUtils authUtils) {
        this.articleRepository = articleRepository;
        this.authUtils = authUtils;
    }

    public Article uploadArticle(ArticleDTO articleDTO) {

            var consultant = authUtils.getCurrentUser();
            var article = new Article(articleDTO.title(),
                    articleDTO.subtitle(),
                    articleDTO.content(),
                    (Consultant) consultant,
                    articleDTO.tags());
            articleRepository.save(article);
            return article;
    }

    @Cacheable(value = "article_search", keyGenerator = "customCacheKeyGenerator")
    public PageResponse<ArticleSearchResponse> searchArticle(String query, Pageable pageable) {
        if (query.isEmpty() || query.isBlank()) {
            return PageResponse.from(Page.empty());
        }
        Set<ArticleSearchDTO> result = articleRepository.queryArticle(query, pageable.getPageSize(), pageable.getPageNumber());
        var total = result.stream().findFirst().isPresent()? result.stream().findFirst().get().total() : 0;
        var setOfArticleResponse = result.stream()
                .map(this::convertFromSearchDTOtoSearchResponse)
                .collect(Collectors.toSet());

        return PageResponse.fromSet(pageable, setOfArticleResponse, total);
    }



    private ArticleSearchResponse convertFromSearchDTOtoSearchResponse(ArticleSearchDTO articleSearchDTO) {
        return new ArticleSearchResponse(
                articleSearchDTO.id(),
                articleSearchDTO.title(),
                articleSearchDTO.subtitle(),
                articleSearchDTO.authorFirstName(),
                articleSearchDTO.authorLastName(),
                articleSearchDTO.upvotes(),
                articleSearchDTO.numberOfComment(),
                articleSearchDTO.reads(),
                articleSearchDTO.publishedAt().toString(),
                articleSearchDTO.rank(),
                articleSearchDTO.highlighted(),
                articleSearchDTO.tags()
                );
    }
    private ArticleSearchResponse convertFromArticleToArticleSearchResponse(Article article) {
        return new ArticleSearchResponse(
                article.getId(),
                article.getTitle(),
                article.getSubtitle(),
                article.getConsultant().getUserNameObject().getFirstName(),
                article.getConsultant().getUserNameObject().getLastName(),
                article.getLikeUserIds().length,
                article.getComments().size(),
                article.getNumberOfReads(),
                article.getPublished_at().toString(),
                1.0F,
                null,
                article.getTags()
        );
    }

    public PageResponse<ArticleSearchResponse> getConsultantArticle(Consultant consultant, Pageable pageable) {
        var pageOfArticle = articleRepository.queryArticleByConsultant(consultant, pageable);
        var pageOfArticleSearchResponse = pageOfArticle.map(this::convertFromArticleToArticleSearchResponse);
        return PageResponse.from(pageOfArticleSearchResponse);
    }

    @Cacheable(value = "articleRecommendationResponse", keyGenerator = "customCacheKeyGenerator")
    public PageResponse<ArticleSearchResponse> recommendArticles(Pageable pageable,Patient patient) {
        Set<ArticleSearchDTO> results = articleRepository
                .recommendArticleToPatient(patient.getId(), pageable.getPageSize(), pageable.getPageNumber());
        Set<ArticleSearchResponse> articleSearchResponseSet = results.stream()
                .map(this::convertFromSearchDTOtoSearchResponse)
                .collect(Collectors.toSet());
        return PageResponse.fromSet(pageable,articleSearchResponseSet,20 );
    }
}
