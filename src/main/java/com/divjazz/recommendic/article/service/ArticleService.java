package com.divjazz.recommendic.article.service;

import com.divjazz.recommendic.article.dto.ArticleDTO;
import com.divjazz.recommendic.article.dto.ArticleSearchDTO;
import com.divjazz.recommendic.article.dto.ArticleSearchResponse;
import com.divjazz.recommendic.article.dto.ArticleUpload;
import com.divjazz.recommendic.article.event.ArticleEvent;
import com.divjazz.recommendic.article.event.EventType;
import com.divjazz.recommendic.article.mapper.ArticleMapper;
import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.article.repository.ArticleRepository;
import com.divjazz.recommendic.article.repository.ArticleRepositoryCustom;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.repository.ConsultantProfileRepository;
import com.divjazz.recommendic.user.service.ConsultantService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final AuthUtils authUtils;
    private final ApplicationEventPublisher eventPublisher;
    private final ArticleRepositoryCustom articleRepositoryCustom;
    private final ConsultantService consultantService;

    public ArticleDTO uploadArticle(ArticleUpload articleUpload) {

        var consultant = (Consultant) authUtils.getCurrentUser();

        var article = new Article(articleUpload.title(),
                articleUpload.subtitle(),
                articleUpload.content(),
                consultant,
                articleUpload.tags());
        articleRepository.save(article);

        return new ArticleDTO(
                article.getTitle(),
                article.getSubtitle(),
                "",
                article.getTags(),
                article.getLikeUserIds().length,
                consultant.getProfile().getUserName().getFirstName(),
                article.getNumberOfReads(),
                article.getPublished_at() == null ? null
                        : article.getPublished_at().toString()
                );
    }

    @Cacheable(value = "article_search", keyGenerator = "customCacheKeyGenerator")
    @Transactional(readOnly = true)
    public Stream<ArticleSearchResponse> searchArticle(String query, Pageable pageable) {
        if (query.isEmpty() || query.isBlank()) {
            Set<ArticleSearchDTO> result = articleRepository.queryTopArticle(pageable.getPageSize(), pageable.getPageNumber());
            return result.stream()
                    .map(this::convertFromSearchDTOtoSearchResponse);
        }
        Set<ArticleSearchDTO> result = articleRepository.queryArticle(query, pageable.getPageSize(), pageable.getPageNumber());
        return result.stream()
                .map(this::convertFromSearchDTOtoSearchResponse);
    }

    @Transactional(readOnly = true)
    public PageResponse<ArticleSearchResponse> searchPageArticle(String query, Pageable pageable) {
        if (query.isEmpty() || query.isBlank()) {
            var result = articleRepository.queryTopArticle(pageable.getPageSize(), pageable.getPageNumber());

            return PageResponse.fromSet(pageable,
                    result.stream()
                            .map(this::convertFromSearchDTOtoSearchResponse).collect(Collectors.toSet()),
                    result.isEmpty()? 0 : result.stream().findFirst().get().total());
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
        if (authUtils.getCurrentUser().getUserType() == UserType.PATIENT) {
            ArticleEvent articleEvent = new ArticleEvent(authUtils.getCurrentUser(), EventType.ARTICLE_PATIENT_REQUESTED, article);
            eventPublisher.publishEvent(articleEvent);
        }
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

    private ArticleSearchResponse convertFromArticleToArticleSearchResponse(Article article, Consultant consultant) {
        var consultantProfile = consultantService.getConsultantProfile(consultant);
        return new ArticleSearchResponse(
                article.getId(),
                article.getTitle(),
                article.getSubtitle(),
                consultantProfile.getUserName().getFirstName(),
                consultantProfile.getUserName().getLastName(),
                article.getLikeUserIds().length,
                article.getComments().size(),
                article.getNumberOfReads(),
                article.getPublished_at().toString(),
                1.0F,
                null,
                article.getTags()
        );
    }
    @Transactional
    public PageResponse<ArticleSearchResponse> getByConsultant(Consultant consultant, Pageable pageable) {
        var pageOfArticle = articleRepository.queryArticleByConsultant(consultant, pageable);
        var pageOfArticleSearchResponse = pageOfArticle
                .map(article -> convertFromArticleToArticleSearchResponse(article, consultant));
        return PageResponse.from(pageOfArticleSearchResponse);
    }
    @Transactional
    public Stream<Article> getArticleByConsultant(Consultant consultant) {
        return articleRepository.findArticleByConsultant(consultant);
    }

    @Cacheable(value = "articleRecommendationResponse", keyGenerator = "customCacheKeyGenerator")
    public PageResponse<ArticleSearchResponse> recommendArticles(Pageable pageable, Patient patient) {
        Set<ArticleSearchDTO> results = articleRepositoryCustom.recommendArticleForPatient(pageable.getPageNumber(), pageable.getPageSize());
        Set<ArticleSearchResponse> articleSearchResponseSet = results.stream()
                .map(this::convertFromSearchDTOtoSearchResponse)
                .collect(Collectors.toSet());
        return PageResponse.fromSet(pageable, articleSearchResponseSet, 20);
    }
}
