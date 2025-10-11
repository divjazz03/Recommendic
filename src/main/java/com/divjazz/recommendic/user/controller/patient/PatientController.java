package com.divjazz.recommendic.user.controller.patient;

import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.user.controller.patient.payload.*;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        var infoResponse = patientService.createPatient(requestParams);

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
    @GetMapping("/profiles")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<PatientProfileResponse>> getPatientProfile() {
        var patientProfile = patientService.getThisPatientProfile();
        return ResponseEntity.ok(getResponse(patientProfile, HttpStatus.OK));
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
            @PathVariable @Parameter(name = "targetId", description = "User id") String userId,
            @RequestBody
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Onboarding Request")
            PatientOnboardingRequest request
    ) {

        patientService.handleOnboarding(userId, request.medicalCategories());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profiles/details")
    @Operation(summary = "Get full patient profile for this authenticated user")
    public ResponseEntity<Response<PatientProfileDetails>> getMyProfileDetails() {
        var patientProfileDetails = patientService.getMyProfileDetails();
        return ResponseEntity
                .ok(getResponse(patientProfileDetails, HttpStatus.OK));
    }

    @GetMapping("/recommendations/consultants")
    @Operation(summary = "Get Consultant Recommendations for this particular user")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<ConsultantRecommendationResponse>> retrieveRecommendationsBasedOnCurrentPatientId() {

        var result = patientService.getRecommendationForPatient();
        var response = getResponse(result,
                HttpStatus.OK
        );
        return ResponseEntity.ok().body(response);
    }

    public record PatientOnboardingRequest(List<String> medicalCategories) {}

}
