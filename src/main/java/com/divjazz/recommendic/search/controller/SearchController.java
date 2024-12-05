package com.divjazz.recommendic.search.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.externalApi.openFDA.OpenFDAQuery;
import com.divjazz.recommendic.search.service.SearchService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.divjazz.recommendic.externalApi.openFDA.OpenFDAQuery.Sort.valueOf;
import static com.divjazz.recommendic.utils.RequestUtils.getErrorResponse;
import static com.divjazz.recommendic.utils.RequestUtils.getResponse;


@RestController("api/v1/search")
public class SearchController {

    private final SearchService searchService;
    private final GeneralUserService userService;
    private final OpenFDAQuery openFDAQuery;

    public SearchController(SearchService searchService,
                            GeneralUserService userService,
                            OpenFDAQuery openFDAQuery) {
        this.searchService = searchService;
        this.userService = userService;
        this.openFDAQuery = openFDAQuery;
    }

    @GetMapping("auth/")
    @Async
    public CompletableFuture<ResponseEntity<Response>> search(@RequestParam(name = "user_Id") String userId,
                                                              @RequestParam(name = "category") String category,
                                                              @RequestParam(name = "query") String query,
                                                              Authentication authentication,
                                                              HttpServletRequest request){
        if (isValidUser(userId, authentication)) {
            var results = searchService.executeQuery(query, userId, category);
            var response = getResponse(request, Map.of("data", results), "Search Successful", HttpStatus.OK);
            return CompletableFuture.completedFuture(new ResponseEntity<>(response,HttpStatus.OK));
        }

        var response = getErrorResponse(request, HttpStatus.FORBIDDEN, new RuntimeException("User not authenticated"));
        return CompletableFuture.completedFuture(new ResponseEntity<>(response, HttpStatus.FORBIDDEN));

    }

    @GetMapping("drug/")
    @Async
    public CompletableFuture<ResponseEntity<Response>> search(@RequestParam("search_param") String searchParam,
                                                              @RequestParam("search_term") String searchTerm,
                                                              @RequestParam("limit") String limit,
                                                              @RequestParam("count") String count,
                                                              @RequestParam("sort") String sort, HttpServletRequest httpServletRequest) {
        try {
            //search terms are separated by ':'
            boolean sortIsNullButLimitIsNot = Objects.isNull(sort) && Objects.nonNull(limit);
            boolean sortAndLimitAreNotNull = Objects.nonNull(sort) && Objects.nonNull(limit);
            if (Objects.isNull(count)) {
                if (sortAndLimitAreNotNull) {
                    var result = getResponse(httpServletRequest, Map.of("data",openFDAQuery.queryDrugs(valueOf(sort), Integer.parseInt(limit), searchParam,
                                    splitSearchTerms(searchTerm))),
                            "query was successful", HttpStatus.OK);
                    return CompletableFuture.completedFuture(new ResponseEntity<>(result, HttpStatus.OK));

                } else if (sortIsNullButLimitIsNot) {
                    var result = getResponse(httpServletRequest, Map.of("data",openFDAQuery.queryDrugs(Integer.parseInt(limit),searchParam, splitSearchTerms(searchTerm))),
                            "query was successful", HttpStatus.OK);
                    return CompletableFuture.completedFuture(new ResponseEntity<>(result, HttpStatus.OK));
                } else if (Objects.nonNull(sort)) {
                    var result = getResponse(httpServletRequest, Map.of("data",openFDAQuery.queryDrugs(valueOf(sort), searchParam, splitSearchTerms(searchTerm))),
                            "query was successful", HttpStatus.OK);
                    return CompletableFuture.completedFuture(new ResponseEntity<>(result, HttpStatus.OK));
                } else {
                    var result = getResponse(httpServletRequest, Map.of("data",openFDAQuery.queryDrugs(searchParam,
                                    splitSearchTerms(searchTerm))),
                            "query was successful", HttpStatus.OK);
                    return CompletableFuture.completedFuture(new ResponseEntity<>(result, HttpStatus.OK));
                }
            } else {
                if (sortAndLimitAreNotNull) {
                    var result = getResponse(httpServletRequest, Map.of("data",openFDAQuery.queryDrugs(valueOf(sort), Integer.parseInt(limit), searchParam,
                                    splitSearchTerms(searchTerm))),
                            "query was successful", HttpStatus.OK);
                    return CompletableFuture.completedFuture(new ResponseEntity<>(result, HttpStatus.OK));

                } else if (sortIsNullButLimitIsNot) {
                    var result = getResponse(httpServletRequest, Map.of("data",openFDAQuery.queryDrugs(Integer.parseInt(limit),searchParam, splitSearchTerms(searchTerm),count)),
                            "query was successful", HttpStatus.OK);
                    return CompletableFuture.completedFuture(new ResponseEntity<>(result, HttpStatus.OK));
                } else if (Objects.nonNull(sort)) {
                    var result = getResponse(httpServletRequest, Map.of("data",openFDAQuery.queryDrugs(valueOf(sort), searchParam, splitSearchTerms(searchTerm),count)),
                            "query was successful", HttpStatus.OK);
                    return CompletableFuture.completedFuture(new ResponseEntity<>(result, HttpStatus.OK));
                } else {
                    var result = getResponse(httpServletRequest, Map.of("data",openFDAQuery.queryDrugs(searchParam,
                                    splitSearchTerms(searchTerm), count)),
                            "query was successful", HttpStatus.OK);
                    return CompletableFuture.completedFuture(new ResponseEntity<>(result, HttpStatus.OK));

                }

            }
        } catch (NumberFormatException e) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(getErrorResponse(httpServletRequest, HttpStatus.EXPECTATION_FAILED, e), HttpStatus.EXPECTATION_FAILED));
        }

    }

    @GetMapping("/")
    @Async
    public CompletableFuture<ResponseEntity<Response>> search(@RequestParam(name = "category") String category,
                                                              @RequestParam(name = "query") String query,
                                                              HttpServletRequest request) {
        var results = searchService.executeQuery(query, category);
        var response = getResponse(request, Map.of("data", results), "Search Successful", HttpStatus.OK);
        return CompletableFuture.completedFuture(new ResponseEntity<>(response, HttpStatus.OK));
    }

    private boolean isValidUser(String userId, Authentication authentication) {
        var authUserId = userService.retrieveUserByUsername(((UserDetails) authentication.getPrincipal()).getUsername()).getUserId();
        return (userId.equals(authUserId) && authentication.isAuthenticated());
    }

    private List<String> splitSearchTerms(String searchTerm) {
        return List.of(searchTerm.split(":"));
    }


}
