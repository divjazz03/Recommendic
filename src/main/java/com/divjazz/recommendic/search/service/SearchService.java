package com.divjazz.recommendic.search.service;

import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.search.dto.SearchResult;
import com.divjazz.recommendic.search.enums.Category;
import com.divjazz.recommendic.search.model.Search;
import com.divjazz.recommendic.search.repository.SearchRepository;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.AdminService;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.PatientService;
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

    private final AdminService adminService;

    public SearchService(SearchRepository searchRepository, ConsultantService consultantService, GeneralUserService userService, ConsultationService consultationService, PatientService patientService, AdminService adminService) {
        this.searchRepository = searchRepository;
        this.consultantService = consultantService;
        this.userService = userService;
        this.consultationService = consultationService;
        this.patientService = patientService;
        this.adminService = adminService;
    }

    /**
    The Search is determined by the 0th index of String Array of the query after it has been split
     @param query This represents the query string which could either be "category" or the consultant name
     @param userId This represents the user's id who made the search
     @return Returns a Set of Consultant Objects
     */
    public Set<SearchResult> executeQuery(String query, String userId, String category){
        var currentUser = userService.retrieveUserByUserId(userId).orElseThrow(UserNotFoundException::new);
        return handleSearchForAuthorizedUsers(query, currentUser, category);
    }
    public Set<SearchResult> executeQuery(String query, String category) {
        return handleSearchForUnauthorizedUsers(query, category);
    }

    private Set<Search> retrieveSearchesByUserId(String userId){
        var currentUser = userService.retrieveUserByUserId(userId);

        return currentUser.map(searchRepository::findByOwnerOfSearch).orElse(Collections.emptySet());
    }

    private Set<SearchResult> handleSearchForAuthorizedUsers(String query, User currentUser, String category){
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
                                    consultation.isAccepted()
                            )).collect(Collectors.toSet());
                        }
                        results.add( new SearchResult(
                                Map.of("consultations", consultationsResult)
                        ));
                    }
                    case CONSULTATION -> {
                        var consultations = patientService.getConsultations(currentUser.getUserId());
                        Set<ConsultationResponse> consultationsResult = new HashSet<>(10);
                        if (!consultations.isEmpty()) {
                            consultationsResult = consultations.stream().map(consultation -> new ConsultationResponse(
                                    consultation.getDiagnosis(),
                                    consultation.getConsultationTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                    consultation.getPatient().getUserNameObject().getFullName(),
                                    consultation.getConsultant().getUserNameObject().getFullName(),
                                    consultation.getStatus().toString(),
                                    consultation.isAccepted()
                            )).collect(Collectors.toSet());
                        }
                        var consultants = consultantService.searchSomeConsultantsByQuery(query);

                        results.add( new SearchResult(
                                Map.of("consultations", consultationsResult,
                                        "consultantsByQuery", consultants)
                        ));
                    }

                    case SEARCH_HISTORY -> results.addAll(handleSearchBasedOnHistory(currentUser.getUserId()));

                    default -> results.add(new SearchResult(Collections.emptyMap()));
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
                                    consultation.isAccepted()
                            )).collect(Collectors.toSet());
                        }
                        var consultant = consultantService.retrieveConsultantByUserId(currentUser.getUserId()).orElseThrow(UserNotFoundException::new);
                        var patients = patientService.findPatientsByMedicalCategories(Collections.singleton(consultant.getMedicalCategory()));
                        results.add(new SearchResult(
                                Map.of("consultations", consultationsResult ,
                                        "patients", patients)
                        ));
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
                                    consultation.isAccepted()
                            )).collect(Collectors.toSet());
                        }

                        results.add(new SearchResult(
                                Map.of("consultations", consultationsResult)
                        ));
                    }

                    case SEARCH_HISTORY -> results.addAll(handleSearchBasedOnHistory(currentUser.getUserId()));

                    default -> results.add(new SearchResult(Collections.emptyMap()));
                    //TODO: ADD MORE FUNCTIONALITY TO THE SEARCH ONCE MORE CATEGORIES EXIST;

                }
            }

            case ADMIN -> {

                switch (searchCategoryEnum) {
                    // All categories in search option from the frontend
                    case ALL -> {


                        var consultants = consultantService.getAllUnCertifiedConsultants();
                        var assignments = adminService.getAllAssignmentsAssigned(currentUser.getUserId());
                        if (!consultants.isEmpty() && !assignments.isEmpty()){
                            results.add(new SearchResult(
                                    Map.of("assignments", assignments,
                                            "unCertifiedConsultants", consultants)
                            ));
                        } else if (!consultants.isEmpty()) {
                            results.add(new SearchResult(
                                    Map.of("unCertifiedConsultants", consultants)
                            ));
                        } else if (!assignments.isEmpty()){
                            results.add(new SearchResult(
                                    Map.of("assignments", assignments)
                            ));
                        }

                    }
                    case ASSIGNMENT -> {
                        var assignments = adminService.getAllAssignmentsAssigned(currentUser.getUserId());
                        if (!assignments.isEmpty()) {
                            results.add(new SearchResult(
                                    Map.of("assignments", assignments)
                            ));
                        }
                    }
                    case SEARCH_HISTORY -> results.addAll(handleSearchBasedOnHistory(currentUser.getUserId()));

                    default -> results.add(new SearchResult(Collections.emptyMap()));
                    //TODO: ADD MORE FUNCTIONALITY TO THE SEARCH ONCE MORE CATEGORIES EXIST;

                }

            }
        }
        return results;
    }

    private Set<SearchResult> handleSearchForUnauthorizedUsers(String query, String category ){
//        var categoryEnum = Category.valueOf(category.toUpperCase().trim());
//
//        switch (categoryEnum){
//            case ALL ->
//        }
        return Collections.emptySet();
    }

    private Set<SearchResult> handleSearchBasedOnHistory(String userId){
        var temporarySearchResults = new HashSet<SearchResult>(20);
        var searches = retrieveSearchesByUserId(userId);
        searches.stream()
                .map(search -> consultantService.searchSomeConsultantsByQuery(search.getQuery()))
                .map(consultantInfoResponses -> new SearchResult(Map.of("consultantsByHistory", consultantInfoResponses)))
                .forEach(temporarySearchResults::add);

        return temporarySearchResults;

    }
}
