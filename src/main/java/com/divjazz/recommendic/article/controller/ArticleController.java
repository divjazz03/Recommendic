package com.divjazz.recommendic.article.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.article.dto.ArticleDTO;
import com.divjazz.recommendic.article.dto.ArticleUpload;
import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.article.service.ArticleService;
import com.divjazz.recommendic.general.PageResponse;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.security.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.divjazz.recommendic.security.utils.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleController {

    private final ArticleService articleService;

    private final AuthUtils authUtils;

    public ArticleController(ArticleService articleService, AuthUtils authUtils) {
        this.articleService = articleService;
        this.authUtils = authUtils;
    }

    @PostMapping("/")
    public ResponseEntity<Response<Article>> uploadArticle(@RequestBody ArticleUpload articleUpload) {

        var user = authUtils.getCurrentUser();
        var result = articleService.uploadArticle(new ArticleDTO(articleUpload.title(), articleUpload.content(), user));

        return new ResponseEntity<>(getResponse(result,"successful", HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<Response<PageResponse<Article>>> retrieveArticle(@PageableDefault Pageable pageable) {
        var user = authUtils.getCurrentUser();
        var results = articleService.recommendArticles(pageable,(Patient) user);
        return new ResponseEntity<>(getResponse(results,"SuccessFul", HttpStatus.OK), HttpStatus.OK);
    }
}
