package com.divjazz.recommendic.user.controller.patient;

import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.divjazz.recommendic.utils.RequestUtils.getErrorResponse;
import static com.divjazz.recommendic.utils.RequestUtils.getResponse;


@RestController
@RequestMapping("/api/v1/patient")
public class PatientController {

    Logger logger = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;
    private final RecommendationService recommendationService;


    public PatientController(PatientService patientService, RecommendationService recommendationService) {
        this.patientService = patientService;
        this.recommendationService = recommendationService;
    }

    @PostMapping("create")
    @Operation(summary = "Register a Patient User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    content = @Content(examples = {
                            @ExampleObject(value = VALID_REQUEST, name = "validRequest", description = "validRequest")
                    })))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Patient successfully created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Authentication failed",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "403",
                    description = "You do not have the permission to perform this action",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))})
    })
    public ResponseEntity<Response> createPatient(@RequestBody @Valid PatientRegistrationParams requestParams) {
        RequestContext.reset();
        RequestContext.setUserId(0L);

        PatientDTO patient = new PatientDTO(
                new UserName(requestParams.firstName(), requestParams.lastName()),
                requestParams.email(), requestParams.phoneNumber(),
                switch (requestParams.gender().toUpperCase()) {
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
                Map.of("id", infoResponse.patientId(),
                        "last_name", infoResponse.lastName(),
                        "first_name", infoResponse.firstName(),
                        "phone_number", infoResponse.phoneNumber(),
                        "address", infoResponse.address())
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("patients")
    public ResponseEntity<Response> patients(@ParameterObject Pageable pageable,
                                             HttpServletRequest httpServletRequest) {
        var patients = patientService.getAllPatients(pageable);
        Function<Set<MedicalCategory>, String[]> medicalCategoriesToStringArrayFunction = (Set<MedicalCategory> medicalCategories) ->
                medicalCategories
                        .stream()
                        .map(Enum::name)
                        .toArray(String[]::new);

        var patientDTOSet = patients.stream()
                .map(patient -> new PatientDTO(
                        patient.getUserNameObject(),
                        patient.getEmail(),
                        patient.getPhoneNumber(),
                        patient.getGender(),
                        patient.getAddress(),
                        null,
                        medicalCategoriesToStringArrayFunction.apply(patient.getMedicalCategories())

                ));
        var response = getResponse(httpServletRequest,
                Map.of("patients", patientDTOSet),
                "Success in retrieving the Patient Users",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @DeleteMapping("delete")
    public ResponseEntity<Response> deletePatient(@RequestParam("patient_id") String patientId) {

        patientService.deletePatientByUserId(patientId);
        var response = new Response(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                HttpStatus.OK.value(),
                "",
                HttpStatus.OK,
                "Successfully deleted patient",
                "",
                null

        );
        RequestContext.setUserId(0L);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("recommendations")
    public ResponseEntity<Response> retrieveRecommendationsBasedOnCurrentPatientId(@RequestParam("patient_id") Long id) {

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
    }

    private static final String VALID_REQUEST = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "email": "johnDoe@gmail.com",
                "password": "password",
                "phoneNumber": "+2347044849392",
                "gender": "Male",
                "zipCode": "123456",
                "city": "Ibadan",
                "state": "Oyo",
                "country": "Nigeria",
                "categoryOfInterest": ["Dentistry", "Gynecology"]
            }
            """;

}
