package com.divjazz.recommendic.search.service;

import com.divjazz.recommendic.appointment.dto.AppointmentDTO;
import com.divjazz.recommendic.appointment.mapper.AppointmentMapper;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.article.dto.ArticleSearchResponse;
import com.divjazz.recommendic.article.service.ArticleService;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.mapper.ConsultationMapper;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.search.dto.SearchResult;
import com.divjazz.recommendic.search.enums.Category;
import com.divjazz.recommendic.search.model.Search;
import com.divjazz.recommendic.search.repository.SearchRepository;
import com.divjazz.recommendic.security.exception.AuthenticationException;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.AdminService;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {


    private final SearchRepository searchRepository;
    private final ConsultantService consultantService;
    private final GeneralUserService userService;
    private final PatientService patientService;
    private final ConsultationService consultationService;
    private final AppointmentService appointmentService;
    private final ArticleService articleService;
    private final AdminService adminService;
    private final AuthUtils authUtils;


    /**
     * The Search is determined by the 0th index of String Array of the query after it has been split
     *
     * @param query This represents the query string which could either be "category" or the consultant name
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
                        var consultations = consultationService.retrieveConsultationDetailByPatientId(currentUser.getUserId());
                        var appointments = appointmentService.getAppointmentDetailsByPatientId(currentUser.getUserId());
                        var articles = articleService.searchArticle(
                                query,
                                Pageable.ofSize(10)
                        );
                        Set<AppointmentDTO> appointmentDTOSet = appointments
                                .map(AppointmentMapper::appointmentProjectionToDTO)
                                .limit(10).collect(Collectors.toUnmodifiableSet());
                        Set<ConsultationResponse> consultationsResult = consultations
                                .map(ConsultationMapper::consultationToConsultationResponse)
                                .limit(10).collect(Collectors.toUnmodifiableSet());

                        Set<ConsultantInfoResponse> consultants = consultantService.searchSomeConsultantsByQuery(query);
                        results.addAll(Set.of(
                                new SearchResult(Category.ARTICLE, articles.collect(Collectors.toUnmodifiableSet())),
                                new SearchResult(Category.CONSULTATION, consultationsResult),
                                new SearchResult(Category.CONSULTANTS, consultants),
                                new SearchResult(Category.APPOINTMENT, appointmentDTOSet)
                        ));
                    }

                    case CONSULTATION -> {
                        var consultations = consultationService.retrieveConsultationDetailByPatientId(currentUser.getUserId());
                        Set<ConsultationResponse> consultationsResult = consultations
                                .map(ConsultationMapper::consultationToConsultationResponse)
                                .limit(10).collect(Collectors.toSet());
                        results.add(new SearchResult(Category.CONSULTATION, consultationsResult));
                    }

                    case SEARCH_HISTORY -> results.addAll(handleSearchBasedOnHistory(currentUser.getUserId()));

                    case ARTICLE -> {
                       var articles = articleService.searchArticle(
                                query,
                                Pageable.ofSize(10)
                        );
                       Set<ArticleSearchResponse> articleSet = articles.collect(Collectors.toUnmodifiableSet());
                       results.add(new SearchResult(Category.ARTICLE, articleSet));
                    }

                    case APPOINTMENT -> {
                        var appointments = appointmentService.getAppointmentDetailsByPatientId(currentUser.getUserId());
                        Set<AppointmentDTO> appointmentDTOSet = appointments
                                .map(AppointmentMapper::appointmentProjectionToDTO)
                                .limit(10).collect(Collectors.toSet());

                        results.add(
                                new SearchResult(Category.APPOINTMENT, appointmentDTOSet)
                        );
                    }
                    default -> results.add(new SearchResult(Category.ALL, Collections.EMPTY_SET));
                }

            }

            case CONSULTANT -> {
                switch (searchCategoryEnum) {
                    // All categories in search option from the frontend
                    case ALL -> {
                        var consultations = consultationService.retrieveConsultationDetailByConsultantId(currentUser.getUserId());
                        var appointments = appointmentService.getAppointmentDetailsByConsultantId(currentUser.getUserId());
                        Set<AppointmentDTO> appointmentDTOSet = appointments
                                .map(AppointmentMapper::appointmentProjectionToDTO)
                                .limit(10).collect(Collectors.toSet());
                        Set<ConsultationResponse> consultationsResult = consultations
                                .map(ConsultationMapper::consultationToConsultationResponse)
                                .collect(Collectors.toSet());

                        results.addAll(Set.of(
                                new SearchResult(Category.CONSULTATION, consultationsResult),
                                new SearchResult(Category.APPOINTMENT, appointmentDTOSet)
                        ));
                    }

                    case CONSULTATION -> {
                        var consultations = consultationService.retrieveConsultationDetailByConsultantId(currentUser.getUserId());
                        Set<ConsultationResponse> consultationsResult = consultations
                                .map(ConsultationMapper::consultationToConsultationResponse)
                                .collect(Collectors.toSet());

                        results.add(
                                new SearchResult(Category.CONSULTATION, consultationsResult));
                    }
                    case SEARCH_HISTORY -> results.addAll(handleSearchBasedOnHistory(currentUser.getUserId()));
                    case APPOINTMENT -> {
                        var appointments = appointmentService.getAppointmentDetailsByConsultantId(currentUser.getUserId());
                        Set<AppointmentDTO> appointmentDTOSet = appointments
                                .map(AppointmentMapper::appointmentProjectionToDTO)
                                .limit(10).collect(Collectors.toSet());
                        results.add(new SearchResult(Category.APPOINTMENT, appointmentDTOSet));
                    }
                    case ARTICLE -> {
                        var articles = articleService.searchArticle(
                                query,
                                Pageable.ofSize(10)
                        );
                        Set<ArticleSearchResponse> articleSet = articles.collect(Collectors.toUnmodifiableSet());
                        results.add(new SearchResult(Category.ARTICLE, articleSet));
                    }

                    default -> results.add(new SearchResult(Category.CONSULTATION, Collections.emptySet()));
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
        searchRepository.save(new Search(query, searchCategoryEnum, currentUser));
        return results;
    }

    private Set<SearchResult> handleSearchBasedOnHistory(String userId) {
        var searches = retrieveSearchesByUserId(userId);
        return searches.stream()
                .flatMap(search ->
                        executeQueryForAuthorizedUsers(search.getQuery(),
                                search.getCategory().name()).stream())
                .limit(10)
                .collect(Collectors.toSet());
    }
}
