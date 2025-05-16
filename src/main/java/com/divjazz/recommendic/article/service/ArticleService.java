package com.divjazz.recommendic.article.service;

import com.divjazz.recommendic.article.dto.ArticleDTO;
import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.article.repository.ArticleRepository;
import com.divjazz.recommendic.general.PageResponse;
import com.divjazz.recommendic.general.Sort;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Article uploadArticle(ArticleDTO articleDTO) {

            var consultant = articleDTO.user();
            var article = new Article(articleDTO.title(), articleDTO.content(), (Consultant) consultant);
            articleRepository.save(article);
            return article;
    }

    public PageResponse<Article> searchArticle(String query, Pageable pageable) {
        Page<Article> result = null;
        if (query.isEmpty()) {
            result = articleRepository.findAll(pageable);
        } else {
            result = articleRepository.queryArticle(query, pageable);
        }
        return PageResponse.from(result);
    }

    @Cacheable(value = "articleRecommendationResponse", keyGenerator = "customCacheKeyGenerator")
    public PageResponse<Article> recommendArticles(Pageable pageable,Patient patient) {
        List<Article> result = new ArrayList<>(10);
        int totalPages = 0;
        long totalElements = 0L;
        boolean last = true;
        boolean first = true;
        int size = 0;
        int number = 0;
        boolean sortRmpty = true;
        boolean empty = true;
        boolean sorted = false;
        int numberOfElements = 0;

        for (MedicalCategory category : patient.getMedicalCategories()) {
            var tempResult = articleRepository.findAllByMedicalCategoryOfInterest(category.value(), pageable);
            totalPages = tempResult.getTotalPages();
            totalElements = tempResult.getTotalElements();
            last = tempResult.isLast();
            size = tempResult.getSize();
            number = tempResult.getNumber();
            sortRmpty = tempResult.getSort().isEmpty();
            sorted = tempResult.getSort().isSorted();
            numberOfElements = tempResult.getNumberOfElements();
            first = tempResult.isFirst();
            empty = tempResult.isEmpty();
            result.addAll(tempResult.getContent());
        }

        return new PageResponse<>(result,
                totalPages,
                totalElements,
                last,
                size,
                number,
                new Sort(sortRmpty,sorted),
                numberOfElements,
                first,empty
        );
    }
}
