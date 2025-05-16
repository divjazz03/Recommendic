package com.divjazz.recommendic.search.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.article.service.ArticleService;
import com.divjazz.recommendic.externalApi.openFDA.OpenFDAQuery;
import com.divjazz.recommendic.externalApi.openFDA.OpenFDAResult;
import com.divjazz.recommendic.general.PageResponse;
import com.divjazz.recommendic.search.dto.SearchResult;
import com.divjazz.recommendic.search.service.SearchService;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.service.GeneralUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.divjazz.recommendic.externalApi.openFDA.OpenFDAQuery.Sort.valueOf;
import static com.divjazz.recommendic.security.utils.RequestUtils.getErrorResponse;
import static com.divjazz.recommendic.security.utils.RequestUtils.getResponse;


@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;
    private final GeneralUserService userService;
    private final OpenFDAQuery openFDAQuery;
    private final ArticleService articleService;

    public SearchController(SearchService searchService,
                            GeneralUserService userService,
                            OpenFDAQuery openFDAQuery, ArticleService articleService) {
        this.searchService = searchService;
        this.userService = userService;
        this.openFDAQuery = openFDAQuery;
        this.articleService = articleService;
    }

    @GetMapping("/")
    public ResponseEntity<Response<Set<SearchResult>>> search(
                                           @RequestParam(name = "category") String category,
                                           @RequestParam(name = "query") String query) {

        var results = searchService.executeQueryForAuthorizedUsers(query, category);
        var response = getResponse(results,"Search Successful", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("drug/")
    public ResponseEntity<Response<OpenFDAResult>> search(@RequestParam("search_param") String searchParam,
                                                          @RequestParam("search_term") String searchTerm,
                                                          @RequestParam("limit") String limit,
                                                          @RequestParam("count") String count,
                                                          @RequestParam("sort") String sort, HttpServletRequest httpServletRequest) {
            //search terms are separated by ':'
            boolean sortIsNullButLimitIsNot = Objects.isNull(sort) && Objects.nonNull(limit);
            boolean sortAndLimitAreNotNull = Objects.nonNull(sort) && Objects.nonNull(limit);
            if (Objects.isNull(count)) {

                if (sortAndLimitAreNotNull) {
                    var result = getResponse(openFDAQuery.queryDrugs(valueOf(sort), Integer.parseInt(limit), searchParam,
                                    splitSearchTerms(searchTerm)),
                            "query was successful", HttpStatus.OK);
                    return new ResponseEntity<>(result, HttpStatus.OK);

                } else if (sortIsNullButLimitIsNot) {
                    var result = getResponse(openFDAQuery.queryDrugs(Integer.parseInt(limit), searchParam,
                            splitSearchTerms(searchTerm)),
                            "query was successful", HttpStatus.OK);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                } else if (Objects.nonNull(sort)) {
                    var result = getResponse(openFDAQuery.queryDrugs(valueOf(sort), searchParam, splitSearchTerms(searchTerm)),
                            "query was successful", HttpStatus.OK);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                } else {
                    var result = getResponse(openFDAQuery.queryDrugs(searchParam,
                                    splitSearchTerms(searchTerm)),
                            "query was successful", HttpStatus.OK);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                }
            } else {
                if (sortAndLimitAreNotNull) {
                    var result = getResponse(openFDAQuery.queryDrugs(valueOf(sort), Integer.parseInt(limit), searchParam,
                                    splitSearchTerms(searchTerm)),
                            "query was successful", HttpStatus.OK);
                    return new ResponseEntity<>(result, HttpStatus.OK);

                } else if (sortIsNullButLimitIsNot) {
                    var result = getResponse(openFDAQuery.queryDrugs(Integer.parseInt(limit), searchParam, splitSearchTerms(searchTerm), count),
                            "query was successful", HttpStatus.OK);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                } else if (Objects.nonNull(sort)) {
                    var result = getResponse( openFDAQuery.queryDrugs(valueOf(sort),
                                    searchParam,
                                    splitSearchTerms(searchTerm),
                                    count),
                            "query was successful", HttpStatus.OK);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                } else {
                    var result = getResponse(openFDAQuery.queryDrugs(searchParam,
                                    splitSearchTerms(searchTerm), count),
                            "query was successful", HttpStatus.OK);
                    return new ResponseEntity<>(result, HttpStatus.OK);

                }

            }


    }

    // Should be pageable
    @GetMapping("/article")
    public ResponseEntity<Response<PageResponse<Article>>> searchArticle(@RequestParam(name = "query", defaultValue = "") String query,
                                                  @PageableDefault Pageable pageable) {
        PageResponse<Article> results = articleService.searchArticle(query,
                pageable);
        return ResponseEntity.ok(getResponse(results, "Success", HttpStatus.OK));

    }

    private boolean isValidUser(String userId, Authentication authentication) {
        var authUserId = userService.retrieveUserByEmail(((UserDetails) authentication.getPrincipal()).getUsername()).getUserId();
        return (userId.equals(authUserId) && authentication.isAuthenticated());
    }

    private List<String> splitSearchTerms(String searchTerm) {
        return List.of(searchTerm.split(":"));
    }


}
