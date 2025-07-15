package com.divjazz.recommendic.user.controller.consultant;


import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.service.ConsultantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

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
                "phoneNumber": "+2347044849392",
                "gender": "Male",
                "zipCode": "123456",
                "city": "Ibadan",
                "state": "Oyo",
                "country": "Nigeria",
                "medicalSpecialization": "Dentistry"
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
                "The Consultant was successfully created, Check your email to activate your account",
                HttpStatus.CREATED
        ), HttpStatus.CREATED);


    }

    @GetMapping
    @Operation(summary = "Get Paginated Consultants")
    public ResponseEntity<Response<PageResponse<ConsultantInfoResponse>>> getConsultants(@ParameterObject @PageableDefault Pageable pageable) {

        var data = consultantService.getAllConsultants(pageable);
        var response = getResponse(data, "success", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{consultantId}")
    @Operation(summary = "Delete Consultant by id")
    public ResponseEntity<Response<Void>> deleteConsultant(@PathVariable String consultantId) {
        consultantService.deleteConsultantById(consultantId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
