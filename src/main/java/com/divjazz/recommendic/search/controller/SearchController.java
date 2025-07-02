package com.divjazz.recommendic.search.controller;

import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.article.dto.ArticleSearchResponse;
import com.divjazz.recommendic.article.service.ArticleService;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.search.dto.SearchResult;
import com.divjazz.recommendic.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;


@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Search API")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final ArticleService articleService;



    @GetMapping
    @Operation(summary = "Execute a global search")
    public ResponseEntity<Response<Set<SearchResult>>> search(
            @RequestParam(name = "category") String category,
            @RequestParam(name = "query") String query) {

        var results = searchService.executeQueryForAuthorizedUsers(query, category);
        var response = getResponse(results, "Search Successful", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Should be pageable
    @GetMapping("/article")
    @Operation(summary = "Search for Article")
    public ResponseEntity<Response<PageResponse<ArticleSearchResponse>>> searchArticle(@RequestParam(name = "query", defaultValue = "") String query,
                                                                                       @PageableDefault Pageable pageable) {
        PageResponse<ArticleSearchResponse> results = articleService.searchPageArticle(query,
                pageable);
        return ResponseEntity.ok(getResponse(results, "Success", HttpStatus.OK));

    }

    private Map<String, String> splitSearchTerms(String searchTerm) {
        if (searchTerm.isEmpty() || searchTerm.isBlank()) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        var intermediate = List.of(searchTerm.split("AND"));
        for (String s : intermediate) {
            var finalSplit = s.split(":");
            map.put(finalSplit[0], finalSplit[1]);
        }
        return map;
    }


}
