package com.divjazz.recommendic.article.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.article.dto.ArticleDTO;
import com.divjazz.recommendic.article.dto.ArticleSearchResponse;
import com.divjazz.recommendic.article.dto.ArticleUpload;
import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.article.service.ArticleService;
import com.divjazz.recommendic.general.PageResponse;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.security.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.divjazz.recommendic.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/article")
@Tag(name = "Article API")
public class ArticleController {

    private final ArticleService articleService;

    private final AuthUtils authUtils;

    public ArticleController(ArticleService articleService, AuthUtils authUtils) {
        this.articleService = articleService;
        this.authUtils = authUtils;
    }

    @Operation(summary = "Upload an Article", description = "Must be a consultant to perform this action")
    @PostMapping("/")
    @PreAuthorize("hasRole('CONSULTANT')")
    public ResponseEntity<Response<Article>> uploadArticle(@RequestBody ArticleUpload articleUpload) {
        var result = articleService.uploadArticle(new ArticleDTO(articleUpload.title(), articleUpload.subtitle(), articleUpload.content(), articleUpload.tags()));

        return new ResponseEntity<>(getResponse(result,"successful", HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/")
    @Operation(summary = "Get paged articles")
    public ResponseEntity<Response<PageResponse<ArticleSearchResponse>>> retrieveArticle(@PageableDefault Pageable pageable) {
        var user = authUtils.getCurrentUser();
        var results = switch (user.getUserType()) {
            case PATIENT -> articleService.recommendArticles(pageable,(Patient) user);
            case CONSULTANT -> articleService.getConsultantArticle((Consultant) user, pageable);
            case null, default -> null;
        };

        return new ResponseEntity<>(getResponse(results, "Successful", HttpStatus.OK), HttpStatus.OK);
    }
}
