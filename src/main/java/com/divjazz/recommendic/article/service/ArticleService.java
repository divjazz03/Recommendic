package com.divjazz.recommendic.article.service;

import com.divjazz.recommendic.article.dto.ArticleDTO;
import com.divjazz.recommendic.article.dto.ArticleSearchDTO;
import com.divjazz.recommendic.article.dto.ArticleSearchResponse;
import com.divjazz.recommendic.article.dto.ArticleUpload;
import com.divjazz.recommendic.article.mapper.ArticleMapper;
import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.article.repository.ArticleRepository;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.global.security.utils.AuthUtils;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final AuthUtils authUtils;

    public Article uploadArticle(ArticleUpload articleUpload) {

        var consultant = authUtils.getCurrentUser();
        var article = new Article(articleUpload.title(),
                articleUpload.subtitle(),
                articleUpload.content(),
                (Consultant) consultant,
                articleUpload.tags());
        articleRepository.save(article);
        return article;
    }

    @Cacheable(value = "article_search", keyGenerator = "customCacheKeyGenerator")
    public Stream<ArticleSearchResponse> searchArticle(String query, Pageable pageable) {
        if (query.isEmpty() || query.isBlank()) {
            return Stream.of();
        }
        Set<ArticleSearchDTO> result = articleRepository.queryArticle(query, pageable.getPageSize(), pageable.getPageNumber());
        return result.stream()
                .map(this::convertFromSearchDTOtoSearchResponse);
    }

    public PageResponse<ArticleSearchResponse> searchPageArticle(String query, Pageable pageable) {
        if (query.isEmpty() || query.isBlank()) {
            return PageResponse.from(articleRepository.findAll(pageable).map(this::convertFromArticleToArticleSearchResponse));
        }
        Set<ArticleSearchDTO> result = articleRepository.queryArticle(query, pageable.getPageSize(), pageable.getPageNumber());
        return PageResponse.fromSet(pageable,
                result
                        .stream()
                        .map(this::convertFromSearchDTOtoSearchResponse)
                        .collect(Collectors.toSet()), result.isEmpty() ? 0 : result.stream().findFirst().get().total());
    }

    public ArticleDTO getArticleById(long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article with id: %s either doesn't exist or has been deleted".formatted(id)));
        return ArticleMapper.articleToArticleDTO(article);
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
                articleSearchDTO.publishedAt(),
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

    public PageResponse<ArticleSearchResponse> getByConsultant(Consultant consultant, Pageable pageable) {
        var pageOfArticle = articleRepository.queryArticleByConsultant(consultant, pageable);
        var pageOfArticleSearchResponse = pageOfArticle.map(this::convertFromArticleToArticleSearchResponse);
        return PageResponse.from(pageOfArticleSearchResponse);
    }

    @Cacheable(value = "articleRecommendationResponse", keyGenerator = "customCacheKeyGenerator")
    public PageResponse<ArticleSearchResponse> recommendArticles(Pageable pageable, Patient patient) {
        Set<ArticleSearchDTO> results = articleRepository
                .recommendArticleToPatient(patient.getId(), pageable.getPageSize(), pageable.getPageNumber());
        Set<ArticleSearchResponse> articleSearchResponseSet = results.stream()
                .map(this::convertFromSearchDTOtoSearchResponse)
                .collect(Collectors.toSet());
        return PageResponse.fromSet(pageable, articleSearchResponseSet, 20);
    }
}
