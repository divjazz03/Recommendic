package com.divjazz.recommendic.user.controller.patient;

import com.divjazz.recommendic.recommendation.dto.RecommendationDTO;
import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.search.ConsultantSearchResult;
import com.divjazz.recommendic.search.SearchService;
import com.divjazz.recommendic.user.domain.Response;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.dto.PatientInfoResponse;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.service.PatientService;
import com.divjazz.recommendic.utils.fileUpload.FileResponseMessage;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<Response> createPatient(@RequestBody @Valid PatientRegistrationParams requestParams){
        try {
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

            var infoResponse = patientService.createPatient(patient);

            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.CREATED.value(),
                    "",
                    HttpStatus.CREATED,
                    "The Patient Account was Successfully created",
                    "",
                    Map.of("id",infoResponse.patientId(),
                            "last_name", infoResponse.lastName(),
                            "first_name", infoResponse.firstName(),
                            "phone_number", infoResponse.phoneNumber(),
                            "address", infoResponse.address())
            );

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "",
                    HttpStatus.EXPECTATION_FAILED,
                    e.getMessage(),
                    e.getClass().getName(),
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.EXPECTATION_FAILED);
        } catch (Exception e) {
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    e.getClass().getName(),
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Response> deletePatient(@RequestParam("patient_id")Long patientId){
        try {
            patientService.deletePatientById(patientId);
            var response = new Response(
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.NO_CONTENT.value(),
                    "",
                    HttpStatus.NO_CONTENT,
                    "Successfully deleted patient",
                    "",
                    null

            );
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (Exception e) {
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    e.getClass().getName(),
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }



    @GetMapping("recommendations")
    public ResponseEntity<Response> retrieveRecommendationsBasedOnCurrentPatientId(@RequestParam("patient_id") Long id){
        try {
            Patient patient = patientService.findPatientById(id);
            var recommendations = recommendationService.retrieveRecommendationByPatient(patient);
            var response = new Response(
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.OK.value(),
                    "",
                    HttpStatus.OK,
                    "Successfully retrieved all Recommendations for user " + patient.getReferenceId(),
                    "",
                    Map.of("recommendations", recommendations)
            );
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    e.getClass().getName(),
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    @GetMapping("search")
    public ResponseEntity<Response> retrieveConsultantsAccordingToQuery(@RequestParam("query") String query, @RequestParam("patient_id") Long id){
        try {
            Set<Consultant> consultantsResults = searchService.executeQuery(query,id);
            var consultantsResultsDTO = consultantsResults.stream().map(consultant -> new ConsultantSearchResult(
                    consultant.getUsername(),
                    consultant.getEmail(),
                    consultant.getPhoneNumber(),
                    consultant.getGender().toString().toLowerCase(),
                    consultant.getAddress(),
                    consultant.getMedicalCategory().name().toLowerCase()
            )).collect(Collectors.toSet());
            var response = new Response(
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.OK.value(),
                    "",
                    HttpStatus.OK,
                    "Successfully executed Search",
                    "",
                    Map.of("search_result", consultantsResultsDTO)
            );

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    e.getClass().getName(),
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
