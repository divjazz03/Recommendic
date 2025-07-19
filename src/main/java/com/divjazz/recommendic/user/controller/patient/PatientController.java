package com.divjazz.recommendic.user.controller.patient;

import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.recommendation.model.ConsultantRecommendation;
import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.dto.PatientInfoResponse;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;


@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patient API")
@RequiredArgsConstructor
public class PatientController {

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
                "country": "Nigeria"
            }
            """;
    private final PatientService patientService;
    private final RecommendationService recommendationService;

    @PostMapping
    @Operation(summary = "Register a Patient User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    content = @Content(examples = {
                            @ExampleObject(value = VALID_REQUEST, name = "validRequest", description = "validRequest")
                    })))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Patient successfully created",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403",
                    description = "You do not have the permission to perform this action",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Response<PatientInfoResponse>> createPatient(@RequestBody @Valid PatientRegistrationParams requestParams) {
        PatientDTO patient = new PatientDTO(
                new UserName(requestParams.firstName(), requestParams.lastName()),
                requestParams.email(), requestParams.phoneNumber(),
                switch (requestParams.gender().toUpperCase()) {
                    case "MALE" -> Gender.MALE;
                    case "FEMALE" -> Gender.FEMALE;
                    default -> throw new IllegalArgumentException("No Such Gender");
                },
                new Address(requestParams.city(), requestParams.state(), requestParams.country()),
                requestParams.password()
        );

        var infoResponse = patientService.createPatient(patient);

        var response = getResponse(infoResponse, HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get Paginated Patients")
    public ResponseEntity<Response<PageResponse<PatientInfoResponse>>> patients(@ParameterObject @PageableDefault Pageable pageable) {
        var patients = patientService.getAllPatients(pageable);
        var response = getResponse(patients,
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    @GetMapping("/{userId}")
    public ResponseEntity<Response<PatientInfoResponse>> getPatientDetail(@PathVariable String userId) {
        var patientInfo = patientService.getPatientDetailById(userId);
        return ResponseEntity.ok(getResponse(patientInfo,  HttpStatus.OK));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete Patient by id")
    @PreAuthorize(value = "hasAuthority('ROLE_ADMIN') or #userId.equals(@authUtils.currentUser.userId)")
    public ResponseEntity<Response<Void>> deletePatient(@PathVariable String userId) {
        patientService.deletePatientByUserId(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{userId}/onboard")
    @Operation(summary = "Set Patient Area of Interest")
    public ResponseEntity<Void> onboardingSetListOfMedicalInterests(
            @PathVariable @Parameter(name = "userId", description = "User id") String userId,
            @RequestBody
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Onboarding Request")
            PatientOnboardingRequest request
    ) {

        patientService.handleOnboarding(userId, request.medicalCategories());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/recommendations")
    @Operation(summary = "Get Consultant Recommendations for this particular user")
    public ResponseEntity<Response<Set<ConsultantRecommendation>>> retrieveRecommendationsBasedOnCurrentPatientId(@PathVariable String userId) {

        var result = patientService.getRecommendationForPatient(userId);
        var response = getResponse(result,
                HttpStatus.OK
        );
        return ResponseEntity.ok().body(response);
    }

    public record PatientOnboardingRequest(List<String> medicalCategories) {}

}
