package com.divjazz.recommendic.search.service;

import com.divjazz.recommendic.article.dto.ArticleSearchResponse;
import com.divjazz.recommendic.article.service.ArticleService;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.general.PageResponse;
import com.divjazz.recommendic.search.dto.SearchResult;
import com.divjazz.recommendic.search.enums.Category;
import com.divjazz.recommendic.search.model.Search;
import com.divjazz.recommendic.search.repository.SearchRepository;
import com.divjazz.recommendic.security.exception.AuthenticationException;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.AdminService;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.PatientService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {


    private final SearchRepository searchRepository;
    private final ConsultantService consultantService;
    private final GeneralUserService userService;
    private final ConsultationService consultationService;
    private final PatientService patientService;

    private final ArticleService articleService;

    private final AdminService adminService;

    private final AuthUtils authUtils;

    public SearchService(SearchRepository searchRepository, ConsultantService consultantService, GeneralUserService userService, ConsultationService consultationService, PatientService patientService, ArticleService articleService, AdminService adminService, AuthUtils authUtils) {
        this.searchRepository = searchRepository;
        this.consultantService = consultantService;
        this.userService = userService;
        this.consultationService = consultationService;
        this.patientService = patientService;
        this.articleService = articleService;
        this.adminService = adminService;
        this.authUtils = authUtils;
    }

    /**
     * The Search is determined by the 0th index of String Array of the query after it has been split
     *
     * @param query  This represents the query string which could either be "category" or the consultant name
     * @return Returns a Set of Consultant Objects
     */
    public Set<SearchResult> executeQueryForAuthorizedUsers(String query, String category) {
        if (Objects.nonNull(authUtils.getCurrentUser())) {
            return handleSearchForAuthorizedUsers(query, authUtils.getCurrentUser(), category);
        }
        throw new AuthenticationException("Couldn't get current user");
    }

    public Set<Search> retrieveSearchesByUserId(String userId) {
        var currentUser = userService.retrieveUserByUserId(userId);

        return searchRepository.findByOwnerOfSearchId(currentUser.getUserId());
    }

    private Set<SearchResult> handleSearchForAuthorizedUsers(String query, User currentUser, String category) {
        Set<SearchResult> results = new HashSet<>(20);
        // For authorized users
        var searchCategoryEnum = Category.valueOf(category.toUpperCase().trim());
        var userType = currentUser.getUserType();

        switch (userType) {
            case PATIENT -> {
                switch (searchCategoryEnum) {
                    // All categories in search option from the frontend
                    case ALL -> {
                        var consultations = consultationService.retrieveConsultationsByUserId(currentUser.getUserId());
                        Set<ConsultationResponse> consultationsResult = new HashSet<>(10);
                        if (!consultations.isEmpty()) {
                            consultationsResult = consultations.stream().map(consultation -> new ConsultationResponse(
                                    consultation.getDiagnosis(),
                                    consultation.getConsultationTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                    consultation.getPatient().getUserNameObject().getFullName(),
                                    consultation.getConsultant().getUserNameObject().getFullName(),
                                    consultation.getStatus().toString(),
                                    consultation.getConsultationId(),
                                    consultation.isAccepted()
                            )).collect(Collectors.toSet());
                        }
                        var consultants = consultantService.searchSomeConsultantsByQuery(query);
                        results.addAll(Set.of(
                                new SearchResult(Category.CONSULTATION,consultationsResult),
                                new SearchResult(Category.CONSULTANTS, consultants)
                        ));
                    }
                    case CONSULTATION -> {
                        var consultations = patientService.findAllConsultationForaGivenPatient(currentUser.getUserId());
                        Set<ConsultationResponse> consultationsResult = new HashSet<>(10);
                        if (!consultations.isEmpty()) {
                            consultationsResult = consultations.stream().map(consultation -> new ConsultationResponse(
                                    consultation.getDiagnosis(),
                                    consultation.getConsultationTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                    consultation.getPatient().getUserNameObject().getFullName(),
                                    consultation.getConsultant().getUserNameObject().getFullName(),
                                    consultation.getStatus().toString(),
                                    consultation.getConsultationId(),
                                    consultation.isAccepted()
                            )).collect(Collectors.toSet());
                            results.add(new SearchResult(Category.CONSULTATION, consultationsResult));
                        }
                    }

                    case SEARCH_HISTORY -> results.addAll(handleSearchBasedOnHistory(currentUser.getUserId()));

                    case ARTICLE -> {
                        PageResponse<ArticleSearchResponse> articles = articleService.searchArticle(query, Pageable.ofSize(10));
                        if (!articles.empty()) {
                            var articlesSet = new HashSet<>(articles.content());
                            results.add(new SearchResult(Category.ARTICLE, articlesSet));
                        }
                    }
                    default -> results.add(new SearchResult(Category.ALL, Collections.EMPTY_SET));
                    //TODO: ADD MORE FUNCTIONALITY TO THE SEARCH ONCE MORE CATEGORIES EXIST;
                }

            }

            case CONSULTANT -> {
                switch (searchCategoryEnum) {
                    // All categories in search option from the frontend
                    case ALL -> {
                        var consultations = consultationService.retrieveConsultationsByUserId(currentUser.getUserId());
                        Set<ConsultationResponse> consultationsResult = new HashSet<>(10);
                        if (!consultations.isEmpty()) {
                            consultationsResult = consultations.stream().map(consultation -> new ConsultationResponse(
                                    consultation.getDiagnosis(),
                                    consultation.getConsultationTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                    consultation.getPatient().getUserNameObject().getFullName(),
                                    consultation.getConsultant().getUserNameObject().getFullName(),
                                    consultation.getStatus().toString(),
                                    consultation.getConsultationId(),
                                    consultation.isAccepted()
                            )).collect(Collectors.toSet());
                        }

                        results.add(
                                new SearchResult(Category.CONSULTATION, consultationsResult));
                    }
                    case CONSULTATION -> {
                        var consultations = consultationService.retrieveConsultationsByUserId(currentUser.getUserId());
                        Set<ConsultationResponse> consultationsResult = new HashSet<>(10);
                        if (!consultations.isEmpty()) {
                            consultationsResult = consultations.stream().map(consultation -> new ConsultationResponse(
                                    consultation.getDiagnosis(),
                                    consultation.getConsultationTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                    consultation.getPatient().getUserNameObject().getFullName(),
                                    consultation.getConsultant().getUserNameObject().getFullName(),
                                    consultation.getStatus().toString(),
                                    consultation.getConsultationId(),
                                    consultation.isAccepted()
                            )).collect(Collectors.toSet());
                        }

                        results.add(
                                new SearchResult(Category.CONSULTATION, consultationsResult));
                    }
                    case SEARCH_HISTORY -> results.addAll(handleSearchBasedOnHistory(currentUser.getUserId()));

                    default -> results.add(new SearchResult(Category.CONSULTATION,Collections.emptySet()));
                    //TODO: ADD MORE FUNCTIONALITY TO THE SEARCH ONCE MORE CATEGORIES EXIST;

                }
            }

            case ADMIN -> {

                switch (searchCategoryEnum) {
                    // All categories in search option from the frontend
                    case ALL -> {


                        var consultants = consultantService.getAllUnCertifiedConsultants();
                        var assignments = adminService.getAllAssignmentsAssigned(currentUser.getUserId());
                        if (!consultants.isEmpty() && !assignments.isEmpty()) {
                            results.addAll(Set.of(
                                    new SearchResult(Category.ASSIGNMENT, assignments),
                                    new SearchResult(Category.CONSULTANTS, consultants)
                            ));
                        } else if (!consultants.isEmpty()) {
                            results.add(new SearchResult(Category.CONSULTANTS, consultants));
                        } else if (!assignments.isEmpty()) {
                            results.add(new SearchResult(Category.ASSIGNMENT, assignments));
                        }

                    }
                    case ASSIGNMENT -> {
                        var assignments = adminService.getAllAssignmentsAssigned(currentUser.getUserId());
                        if (!assignments.isEmpty()) {
                            results.add(new SearchResult(Category.ASSIGNMENT, assignments));
                        }
                    }
                    case SEARCH_HISTORY -> results.addAll(handleSearchBasedOnHistory(currentUser.getUserId()));

                    default -> results.add(new SearchResult(Category.ALL, Collections.emptySet()));
                    //TODO: ADD MORE FUNCTIONALITY TO THE SEARCH ONCE MORE CATEGORIES EXIST;

                }

            }
        }
        searchRepository.save(new Search(query,searchCategoryEnum, currentUser));
        return results;
    }

    private Set<SearchResult> handleSearchBasedOnHistory(String userId) {
        var searches = retrieveSearchesByUserId(userId);
        return searches.stream()
                .flatMap(search ->
                        executeQueryForAuthorizedUsers(search.getQuery(),
                            search.getCategory().name()).stream())
                .limit(20)
                .collect(Collectors.toSet());
    }
}
