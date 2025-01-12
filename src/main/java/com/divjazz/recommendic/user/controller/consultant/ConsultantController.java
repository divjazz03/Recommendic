package com.divjazz.recommendic.user.controller.consultant;


import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.user.controller.UserCreationResponse;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.exception.NoSuchMedicalCategory;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.service.ConsultantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.divjazz.recommendic.utils.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/consultant")
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
        Address address = new Address(requestParams.zipCode(),
                requestParams.city(),
                requestParams.state(),
                requestParams.country());
        MedicalCategory medicalCategory = switch (requestParams.medicalSpecialization().toUpperCase()) {
            case "PEDIATRICIAN" -> MedicalCategory.PEDIATRICIAN;
            case "CARDIOLOGY" -> MedicalCategory.CARDIOLOGY;
            case "ONCOLOGY" -> MedicalCategory.ONCOLOGY;
            case "DERMATOLOGY" -> MedicalCategory.DERMATOLOGY;
            case "ORTHOPEDIC_SURGERY" -> MedicalCategory.ORTHOPEDIC_SURGERY;
            case "NEUROSURGERY" -> MedicalCategory.NEUROSURGERY;
            case "CARDIOVASCULAR_SURGERY" -> MedicalCategory.CARDIOVASCULAR_SURGERY;
            case "GYNECOLOGY" -> MedicalCategory.GYNECOLOGY;
            case "PSYCHIATRY" -> MedicalCategory.PSYCHIATRY;
            case "DENTISTRY" -> MedicalCategory.DENTISTRY;
            case "OPHTHALMOLOGY" -> MedicalCategory.OPHTHALMOLOGY;
            case "PHYSICAL_THERAPY" -> MedicalCategory.PHYSICAL_THERAPY;
            case null, default -> throw new NoSuchMedicalCategory();
        };
        return new ConsultantDTO(userName, userEmail, phoneNumber, gender, address, requestParams.password(), medicalCategory);
    }

    @PostMapping("create")
    @Operation(summary = "Register a Consultant User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    content = @Content(examples = {
                            @ExampleObject(value = VALID_REQUEST, name = "validRequest", description = "validRequest")
                    })))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Admin successfully created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Authentication failed",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "403",
                    description = "You do not have the permission to perform this action",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))})
    })
    public ResponseEntity<Response> createConsultant(@RequestBody @Valid ConsultantRegistrationParams requestParams, HttpServletRequest httpServletRequest) {
        RequestContext.reset();
        RequestContext.setUserId(0L);

        ConsultantDTO consultantDTO = getConsultantDTO(requestParams);
        var consultantInfoResponse = consultantService.createConsultant(consultantDTO);
        return new ResponseEntity<>(getResponse(httpServletRequest,
                Map.of("data", new UserCreationResponse(
                        consultantInfoResponse.consultantId(),
                        consultantInfoResponse.firstName(),
                        consultantInfoResponse.lastName(),
                        consultantInfoResponse.phoneNumber(),
                        consultantInfoResponse.address()
                )),
                "The Consultant was successfully created, Check your email to activate your account",
                HttpStatus.CREATED
        ), HttpStatus.CREATED);


    }

    @GetMapping("consultants")
    public ResponseEntity<Response> getConsultants(@ParameterObject Pageable pageable, HttpServletRequest httpServletRequest) {

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
        var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                HttpStatus.OK.value(),
                "",
                HttpStatus.OK,
                "All Consultants have been successfully retrieved",
                "",
                Map.of("consultants", data)
        );
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }
}
