package com.divjazz.recommendic.article.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.article.dto.ArticleDTO;
import com.divjazz.recommendic.article.dto.ArticleUpload;
import com.divjazz.recommendic.article.service.ArticleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/article")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("upload")
    public ResponseEntity<Response> uploadArticle(@RequestBody ArticleUpload articleUpload, Authentication authentication, HttpServletRequest httpServletRequest) {

        var userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        var response = articleService.uploadArticle(new ArticleDTO(articleUpload.title(),articleUpload.content(),userEmail), httpServletRequest);

        return new ResponseEntity<>(response, response.status());
    }

    @GetMapping("retrieve")
    public ResponseEntity<Response> retrieveArticle(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                    Authentication authentication,
                                                    HttpServletRequest request) {
        var results = articleService.recommendArticles(PageRequest.of(offset, 10), authentication, request);
        return new ResponseEntity<>(results, results.status());
    }
}
