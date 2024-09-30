package com.divjazz.recommendic.search.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.search.enums.Category;
import com.divjazz.recommendic.search.service.SearchService;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController("api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/")
    @Async
    public CompletableFuture<ResponseEntity<Response>> search(@RequestParam(name = "user_Id") String userId,
                                                              @RequestParam(name = "category") String category,
                                                              @RequestParam(name = "query") String query){
        var response = searchService.executeQuery(query, userId, category);

        return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.OK));
    }
    @GetMapping("/filter")
    @Async
    public CompletableFuture<ResponseEntity<Response>> search(@RequestParam(name = "filter") String filter,@RequestParam(name = "category") String category){

        return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.OK));
    }


}
