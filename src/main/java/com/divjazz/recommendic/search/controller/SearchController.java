package com.divjazz.recommendic.search.controller;

import com.divjazz.recommendic.Response;
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
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.divjazz.recommendic.utils.RequestUtils.getErrorResponse;
import static com.divjazz.recommendic.utils.RequestUtils.getResponse;


@RestController("api/v1/search")
public class SearchController {

    private final SearchService searchService;
    private final GeneralUserService userService;

    public SearchController(SearchService searchService, GeneralUserService userService) {
        this.searchService = searchService;
        this.userService = userService;
    }

    @GetMapping("/")
    @Async
    public CompletableFuture<ResponseEntity<Response>> search(@RequestParam(name = "user_Id") String userId,
                                                              @RequestParam(name = "category") String category,
                                                              @RequestParam(name = "query") String query,
                                                              @RequestParam(name = "filter") String filter,
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

    private boolean isValidUser(String userId, Authentication authentication) {
        var authUserId = userService.retrieveUserByUsername(((UserDetails) authentication.getPrincipal()).getUsername()).getUserId();
        return (userId.equals(authUserId) || authentication.isAuthenticated());
    }


}
