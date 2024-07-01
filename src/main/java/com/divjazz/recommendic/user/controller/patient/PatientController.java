package com.divjazz.recommendic.user.controller.patient;

import com.divjazz.recommendic.recommendation.dto.RecommendationDTO;
import com.divjazz.recommendic.recommendation.model.Recommendation;
import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.search.ConsultantSearchResult;
import com.divjazz.recommendic.search.SearchResult;
import com.divjazz.recommendic.search.SearchService;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.service.PatientService;
import com.divjazz.recommendic.utils.fileUpload.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/patient")
public class PatientController {
    private final PatientService patientService;
    private final RecommendationService recommendationService;
    private final SearchService searchService;


    public PatientController(PatientService patientService, RecommendationService recommendationService, SearchService searchService) {
        this.patientService = patientService;

        this.recommendationService = recommendationService;
        this.searchService = searchService;
    }

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseMessage> createPatient(@RequestBody PatientRegistrationParams requestParams){
        PatientDTO patient = new PatientDTO(
                new UserName(requestParams.firstName(), requestParams.lastName()),
                requestParams.email(), requestParams.phoneNumber(),
                switch (requestParams.gender().toUpperCase()){
                    case "MALE" -> Gender.MALE;
                    case "FEMALE" -> Gender.FEMALE;
                    default -> throw new IllegalArgumentException("No Such Gender");
                },
                new Address(requestParams.zipCode(), requestParams.city(), requestParams.state(), requestParams.country()),
                requestParams.password(),
                requestParams.categoryOfInterest()
        );
        return new ResponseEntity<>(patientService.createPatient(patient), HttpStatus.CREATED);
    }
    @DeleteMapping("delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ResponseMessage> deletePatient(@RequestParam("patient_id")String patientId){
        ResponseMessage message = patientService.deletePatientById(patientId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
    }



    @GetMapping("recommendations")
    public ResponseEntity<List<RecommendationDTO>> retrieveRecommendationsBasedOnCurrentPatientId(@RequestParam("patient_id") String id){
        Patient patient = patientService.findPatientById(id);
        List<RecommendationDTO> recommendations = recommendationService.retrieveRecommendationByPatient(patient);
        return ResponseEntity.status(HttpStatus.OK)
                .body(recommendations);
    }
    @GetMapping("search")
    public ResponseEntity<Set<ConsultantSearchResult>> retrieveConsultantsAccordingToQuery(@RequestParam("query") String query, @RequestParam("patients_id") String id){
        Set<Consultant> consultantsResults = searchService.executeQuery(query,id);
        var consultantsResultDTO = consultantsResults.stream().map(consultant -> new ConsultantSearchResult(
                consultant.getUsername(),
                consultant.getEmail(),
                consultant.getPhoneNumber(),
                consultant.getGender().toString().toLowerCase(),
                consultant.getAddress(),
                consultant.getMedicalCategory().name().toLowerCase()
        )).collect(Collectors.toSet());
        return new ResponseEntity<>(consultantsResultDTO, HttpStatus.OK);
    }

}
