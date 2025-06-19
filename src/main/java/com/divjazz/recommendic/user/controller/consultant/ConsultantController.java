package com.divjazz.recommendic.user.controller.consultant;


import com.divjazz.recommendic.Response;
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
import org.jetbrains.annotations.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

import static com.divjazz.recommendic.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/consultant")
@Tag(name = "Consultant API")
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

    public ConsultantController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    private static ConsultantDTO getConsultantDTO(@NotNull ConsultantRegistrationParams requestParams) {
        UserName userName = new UserName(requestParams.firstName(), requestParams.lastName());
        String userEmail = requestParams.email();
        String phoneNumber = requestParams.phoneNumber();
        Gender gender = switch (requestParams.gender().toUpperCase()) {
            case "MALE" -> Gender.MALE;
            case "FEMALE" -> Gender.FEMALE;
            default ->
                    throw new IllegalArgumentException(String.format("Gender %s is not valid", requestParams.gender()));
        };
        Address address = new Address(
                requestParams.city(),
                requestParams.state(),
                requestParams.country());
        return new ConsultantDTO(userName, userEmail, phoneNumber, gender, address, requestParams.password());
    }

    @PostMapping("/create")
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

        ConsultantDTO consultantDTO = getConsultantDTO(requestParams);
        var consultantInfoResponse = consultantService.createConsultant(consultantDTO);
        return new ResponseEntity<>(getResponse(consultantInfoResponse,
                "The Consultant was successfully created, Check your email to activate your account",
                HttpStatus.CREATED
        ), HttpStatus.CREATED);


    }

    @GetMapping("/consultants")
    @Operation(summary = "Get Paginated Consultants")
    public ResponseEntity<Response<Set<ConsultantInfoResponse>>> getConsultants(@ParameterObject Pageable pageable) {

        var data = consultantService.getAllConsultants(pageable).stream()
                .map(consultant -> new ConsultantInfoResponse(
                        consultant.getUserId(),
                        consultant.getUserNameObject().getLastName(),
                        consultant.getUserNameObject().getFirstName(),
                        consultant.getGender().toString().toLowerCase(),
                        consultant.getPhoneNumber(),
                        consultant.getAddress(),
                        consultant.getMedicalCategory().toString().toLowerCase()
                ));
        var response = getResponse(data.collect(Collectors.toSet()), "success", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete Consultant by id")
    public ResponseEntity<Response<Void>> deleteConsultant(@RequestParam("consultant_id") String consultantId) {
        consultantService.deleteConsultantById(consultantId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/onboarding/{userId}")
    @Operation(summary = "Set Consultant Area of Specialization")
    public ResponseEntity<Boolean> onboardingSetListOfMedicalInterests(
            @PathVariable("userId") String userId, @RequestBody ConsultantOnboardingRequest request
    ) {
        boolean value = consultantService.handleOnboarding(userId, request.medicalSpecialization());

        return ResponseEntity.ok(value);
    }

    public record ConsultantOnboardingRequest(String medicalSpecialization) {}
}
