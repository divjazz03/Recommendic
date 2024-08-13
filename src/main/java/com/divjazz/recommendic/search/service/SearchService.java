package com.divjazz.recommendic.search.service;

import com.divjazz.recommendic.search.dto.SearchResult;
import com.divjazz.recommendic.search.model.Search;
import com.divjazz.recommendic.search.repository.SearchRepository;
import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.user.dto.UserInfoResponse;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.divjazz.recommendic.user.utils.RequestUtils.getResponse;

@Service
public class SearchService {


    private final SearchRepository searchRepository;
    private final ConsultantService consultantService;
    private final GeneralUserService userService;

    public SearchService(SearchRepository searchRepository,
                         ConsultantService consultantService,
                         GeneralUserService userService){
        this.searchRepository = searchRepository;
        this.consultantService = consultantService;
        this.userService = userService;
    }

    /**
    The Search is determined by the 0th index of String Array of the query after it has been split
     @param query This represents the query string which could either be "category" or the consultant name
     @param userId This represents the user's id who made the search
     @return Returns a Set of Consultant Objects
     */
    public SearchResult executeQuery(String query, Long userId){
        var resultList = new ArrayList<Map<?,?>>(20);
        User user = userService.retrieveUserById(userId);
        Search search = new Search(query);
        search.setOwnerOfSearch(user);

        Map<String,Set<? extends UserInfoResponse>> userResult = Map.of("consultant",consultantService.searchSomeConsultantsByQuery(query));
        resultList.add(userResult);
        return new SearchResult(resultList);
    }

    public List<Search> retrieveSearchesByUser(Long userId){
        User user = userService.retrieveUserById(userId);
        return searchRepository.findByOwnerOfSearch(user).orElse(List.of());
    }
}
