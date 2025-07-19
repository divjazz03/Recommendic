package com.divjazz.recommendic.user.controller.admin;


import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.dto.AdminInfoResponse;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/admins")
@Tag(name = "Admin API")
@RequiredArgsConstructor
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

    @Operation(summary = "Register an Admin User", description = "Must be a user with super admin access")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Admin successfully created",
                    content = {@Content(mediaType = "application.json")}),
            @ApiResponse(responseCode = "401",
                    description = "Authentication failed",
                    content = {@Content(mediaType = "application.json")}),
            @ApiResponse(responseCode = "403",
                    description = "You do not have the permission to perform this action",
                    content = {@Content(mediaType = "application.json")})
    })
    @PostMapping("/create")
    public ResponseEntity<Response<AdminCredentialResponse>> createAdmin(@RequestBody @Valid AdminRegistrationParams requestParams) {

        AdminCredentialResponse adminResponse = adminService.createAdmin(requestParams);

        var response = getResponse(
                adminResponse,

                HttpStatus.CREATED);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get Paginated Admins",
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
    public ResponseEntity<Response<Set<AdminInfoResponse>>> getAdmins(@ParameterObject Pageable pageable) {

//        var admins = adminService.getAllAdmins(pageable);
//        var data = admins.stream()
//                .map(admin -> new AdminInfoResponse(
//                        admin.getUserId(),
//                        admin.getUserNameObject().getLastName(),
//                        admin.getUserNameObject().getFirstName(),
//                        admin.getEmail(),
//                        admin.getGender().name(),
//                        admin.getAddress()
//                )).collect(Collectors.toSet());
//        var response = getResponse(data,
//                "Successfully Retrieved all admin entries",
//                HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
