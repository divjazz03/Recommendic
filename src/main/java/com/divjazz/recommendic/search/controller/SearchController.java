package com.divjazz.recommendic.search.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.article.service.ArticleService;
import com.divjazz.recommendic.external.openFDA.OpenFDAQuery;
import com.divjazz.recommendic.external.openFDA.OpenFDAResult;
import com.divjazz.recommendic.external.openFDA.OpenFdaSearchType;
import com.divjazz.recommendic.general.PageResponse;
import com.divjazz.recommendic.search.dto.SearchResult;
import com.divjazz.recommendic.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.divjazz.recommendic.external.openFDA.OpenFDAQuery.Sort.valueOf;
import static com.divjazz.recommendic.RequestUtils.getResponse;


@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Search API")
public class SearchController {

    private final SearchService searchService;
    private final OpenFDAQuery openFDAQuery;
    private final ArticleService articleService;

    public SearchController(SearchService searchService,
                            OpenFDAQuery openFDAQuery, ArticleService articleService) {
        this.searchService = searchService;
        this.openFDAQuery = openFDAQuery;
        this.articleService = articleService;
    }

    @GetMapping("/")
    @Operation(summary = "Execute a global search")
    public ResponseEntity<Response<Set<SearchResult>>> search(
                                           @RequestParam(name = "category") String category,
                                           @RequestParam(name = "query") String query) {

        var results = searchService.executeQueryForAuthorizedUsers(query, category);
        var response = getResponse(results,"Search Successful", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/drug")
    @Operation(summary = "Search for drugs")
    public ResponseEntity<Response<OpenFDAResult>> search(@RequestParam(value = "search_term", required = false, defaultValue = "") String searchTerm,
                                                          @RequestParam(value = "limit", required = false, defaultValue = "10") String limit,
                                                          @RequestParam(value = "count", required = false) String count,
                                                          @RequestParam(value = "sort", required = false, defaultValue = "") String sort,
                                                          @RequestParam(value = "type", defaultValue = "label") String type) {

        var openFdaSearchType = OpenFdaSearchType.fromValue(type);
        OpenFDAQuery.Sort sortEnum = sort.isEmpty() || sort.isBlank() ? null : OpenFDAQuery.Sort.valueOf(sort);
        Map<String, String> searchFieldTerm = splitSearchTerms(searchTerm);
        var queryResponse = switch (openFdaSearchType) {
            case DRUG -> openFDAQuery.queryDrugs(sortEnum, Integer.parseInt(limit), searchFieldTerm, count);
            case LABEL -> openFDAQuery.queryLabels(sortEnum, Integer.parseInt(limit), searchFieldTerm,count);
            case ADVERSE_EFFECT -> null;
        };

        var response = getResponse( queryResponse ,"success", HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

    // Should be pageable
    @GetMapping("/article")
    @Operation(summary = "Search for Article")
    public ResponseEntity<Response<PageResponse<Article>>> searchArticle(@RequestParam(name = "query", defaultValue = "") String query,
                                                  @PageableDefault Pageable pageable) {
        PageResponse<Article> results = articleService.searchArticle(query,
                pageable);
        return ResponseEntity.ok(getResponse(results, "Success", HttpStatus.OK));

    }

    private Map<String, String> splitSearchTerms(String searchTerm) {
        if (searchTerm.isEmpty() || searchTerm.isBlank()) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        var intermediate =  List.of(searchTerm.split("AND"));
        for (String s : intermediate) {
            var finalSplit = s.split(":");
            map.put(finalSplit[0], finalSplit[1]);
        }
        return map;
    }


}
