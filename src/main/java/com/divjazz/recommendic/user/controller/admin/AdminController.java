package com.divjazz.recommendic.user.controller.admin;


import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.dto.AdminResponse;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.divjazz.recommendic.utils.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/admin")

public class AdminController {

    private static final String VALID_REQUEST = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "email": "johnDoe@gmail.com",
                "phoneNumber": "+2347044849392",
                "gender": "Male"|"Female",
                "zipCode": "123456",
                "city": "Ibadan",
                "state": "Oyo",
                "country": "Nigeria",
                "medicalSpecialization": "string"
            }
            """;
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Register an Admin User", description = "Must be a user with super admin access")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Admin successfully created",
                    content = {@Content(mediaType = "application.json", schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Authentication failed",
                    content = {@Content(mediaType = "application.json", schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "403",
                    description = "You do not have the permission to perform this action",
                    content = {@Content(mediaType = "application.json", schema = @Schema(implementation = Response.class))})
    })
    @PostMapping("/create")
    public ResponseEntity<Response> createAdmin(@RequestBody @Valid AdminRegistrationParams requestParams, HttpServletRequest httpServletRequest) {
        RequestContext.reset();
        RequestContext.setUserId(0L);
        AdminDTO adminDTO = new AdminDTO(
                new UserName(requestParams.firstName(), requestParams.lastName()), requestParams.email(), requestParams.phoneNumber(),
                switch (requestParams.gender().toUpperCase()) {
                    case "MALE" -> Gender.MALE;
                    case "FEMALE" -> Gender.FEMALE;
                    default -> throw new IllegalArgumentException("No such Gender");
                },
                new Address(requestParams.city(), requestParams.state(), requestParams.country())
        );
        AdminCredentialResponse adminResponse = adminService.createAdmin(adminDTO);
        var data = Map.of(
                "email", adminResponse.email(),
                "password", adminResponse.password());


        var response = getResponse(httpServletRequest,
                data,
                "The Admin Account was Successfully created, Check your Email to enable your Account",
                HttpStatus.CREATED);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all Admins",
            description = "Must be a user with super admin access",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    content = @Content(examples = {
                            @ExampleObject(value = VALID_REQUEST, name = "validRequest", description = "validRequest")
                    })))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Admins found",
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

    @GetMapping("/admins")
    public ResponseEntity<Response> getAdmins(@ParameterObject Pageable pageable, HttpServletRequest httpServletRequest) {

        var admins = adminService.getAllAdmins(pageable);
        var data = admins.stream()
                .map(admin -> new AdminResponse(
                        admin.getUserId(),
                        admin.getUserNameObject().getLastName(),
                        admin.getUserNameObject().getFirstName(),
                        admin.getEmail(),
                        admin.getGender().name(),
                        admin.getAddress()
                ));
        var response = getResponse(httpServletRequest,
                Map.of("admins", data),
                "Successfully Retrieved all admin entries",
                HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
