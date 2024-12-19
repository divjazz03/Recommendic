package com.divjazz.recommendic.article.service;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.article.dto.ArticleDTO;
import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.article.repository.ArticleRepository;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ConsultantService consultantService;
    private final GeneralUserService generalUserService;


    public ArticleService(ArticleRepository articleRepository, ConsultantService consultantService, GeneralUserService generalUserService) {
        this.articleRepository = articleRepository;
        this.consultantService = consultantService;
        this.generalUserService = generalUserService;
    }

    public Response uploadArticle(ArticleDTO articleDTO, HttpServletRequest httpServletRequest) {
        try {
            var consultant = consultantService.retrieveConsultantByEmail(articleDTO.userEmail()).orElseThrow(UserNotFoundException::new);
            var article = new Article(articleDTO.title(), articleDTO.content(),consultant);
            articleRepository.save(article);
            return RequestUtils.getResponse(httpServletRequest, Map.of(), "Article was uploaded successfully", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return RequestUtils.getErrorResponse(httpServletRequest, HttpStatus.NOT_FOUND, e);
        }
    }

    public Response searchArticle(String query, Pageable pageable, HttpServletRequest request) {
        Page<Article> result = null;
        if (query.isEmpty()) {
            result = articleRepository.findAll(pageable);
        } else {
            result = articleRepository.queryArticle(query, pageable);
        }
        return RequestUtils.getResponse(request, Map.of("data", result.getContent()), "successful", HttpStatus.OK);
    }

    public Response recommendArticles(Pageable pageable, Authentication authentication, HttpServletRequest request) {
        List<Article> result = new ArrayList<>(10);
        var patientMedicalCategories = ((Patient) generalUserService
                    .retrieveUserByEmail(((UserDetails)authentication
                        .getPrincipal()).getUsername()))
                .getMedicalCategories();
        patientMedicalCategories.stream()
                .flatMap(medicalCategory -> articleRepository.findAllByMedicalCategoryOfInterest(medicalCategory.toString(), pageable).stream())
                .forEach(result::add);

        return RequestUtils.getResponse(request,Map.of("data", result), "successful", HttpStatus.OK);


    }
}
