package com.divjazz.recommendic.user.controller.consultant;


import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.user.controller.consultant.payload.*;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.service.ConsultantService;
import io.swagger.v3.oas.annotations.Operation;
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

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/consultants")
@Tag(name = "Consultant API")
@RequiredArgsConstructor
public class ConsultantController {

    private static final String VALID_REQUEST = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "email": "johnDoe@gmail.com",
                "password": "password",
                "dateOfBirth": "2003-12-01",
                "gender": "Male",
            }
            """;
    private final ConsultantService consultantService;


    @PostMapping
    @Operation(summary = "Register a Consultant User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    content = @Content(examples = {
                            @ExampleObject(value = VALID_REQUEST, name = "validRequest", description = "validRequest")
                    })))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Admin successfully created",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Authentication failed",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403",
                    description = "You do not have the permission to perform this action",
                    content = {@Content(mediaType = "application/json")})
    })
    public ResponseEntity<Response<ConsultantInfoResponse>> createConsultant(@RequestBody @Valid ConsultantRegistrationParams requestParams) {
        RequestContext.reset();
        RequestContext.setUserId(0L);

        var consultantInfoResponse = consultantService.createConsultant(requestParams);
        return new ResponseEntity<>(getResponse(consultantInfoResponse,

                HttpStatus.CREATED
        ), HttpStatus.CREATED);


    }

    @GetMapping
    @Operation(summary = "Get Paginated Consultants")
    public ResponseEntity<Response<PageResponse<ConsultantInfoResponse>>> getConsultants(@ParameterObject @PageableDefault Pageable pageable) {

        var data = consultantService.getAllConsultants(pageable);
        var response = getResponse(data,  HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{consultantId}")
    public ResponseEntity<Response<ConsultantInfoResponse>> getConsultant(@PathVariable String consultantId) {
        var result = consultantService.getConsultantInfoByUserId(consultantId);
        return ResponseEntity.ok(getResponse(result, HttpStatus.OK));
    }

    @DeleteMapping("/{consultantId}")
    @Operation(summary = "Delete Consultant by id")
    @PreAuthorize(value = "hasAuthority('ROLE_ADMIN') or #consultantId.equals(@authUtils.currentUser.userId)")
    public ResponseEntity<Response<Void>> deleteConsultant(@PathVariable String consultantId) {
        consultantService.deleteConsultantById(consultantId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/profiles")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<Response<ConsultantProfileResponse>> getConsultantProfile() {
        var consultantProfile = consultantService.getConsultantProfileResponse();
        return ResponseEntity.ok(getResponse(consultantProfile, HttpStatus.OK));
    }
    @GetMapping("/profiles/details")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<Response<ConsultantProfileDetails>> getConsultantProfileDetails() {
        var consultantProfileDetails = consultantService.getConsultantProfileDetails();
        return ResponseEntity.ok(getResponse(consultantProfileDetails, HttpStatus.OK));
    }
    @PatchMapping("/profiles")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<Response<ConsultantProfileDetails>> updateConsultantProfileDetails(@RequestBody ConsultantProfileUpdateRequest updateRequest) {
        var consultantProfileDetails = consultantService.updateConsultantProfileDetails(updateRequest);
        return ResponseEntity.ok(getResponse(consultantProfileDetails, HttpStatus.OK));
    }

    @PostMapping("/{userId}/onboard")
    @Operation(summary = "Set Consultant Area of Specialization")
    public ResponseEntity<Boolean> onboardingSetListOfMedicalInterests(
            @PathVariable("userId") String userId, @RequestBody ConsultantOnboardingRequest request
    ) {
        boolean value = consultantService.handleOnboarding(userId, request.medicalSpecialization());
        return ResponseEntity.ok(value);
    }

    public record ConsultantOnboardingRequest(String medicalSpecialization) {}
}
