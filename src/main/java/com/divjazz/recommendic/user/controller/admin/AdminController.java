package com.divjazz.recommendic.user.controller.admin;


import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.dto.AdminResponse;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.service.AdminService;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.divjazz.recommendic.utils.RequestUtils.getErrorResponse;
import static com.divjazz.recommendic.utils.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/admin")

public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("create")
    public ResponseEntity<Response> createAdmin(@RequestBody @Valid AdminRegistrationParams requestParams, HttpServletRequest httpServletRequest){
        RequestContext.reset();
        RequestContext.setUserId(0L);
        try {
            AdminDTO adminDTO = new AdminDTO(
                    new UserName(requestParams.firstName(), requestParams.lastName()), requestParams.email(), requestParams.phoneNumber(),
                    switch (requestParams.gender().toUpperCase()){
                        case "MALE" -> Gender.MALE;
                        case "FEMALE" -> Gender.FEMALE;
                        default -> throw new IllegalArgumentException("No such Gender");
                    },
                    new Address(requestParams.zipcode(), requestParams.city(), requestParams.state(), requestParams.country())
            );
            AdminCredentialResponse adminResponse = adminService.createAdmin(adminDTO);
            var data = Map.of(
                    "email",adminResponse.email(),
                    "password",adminResponse.password());


            var response = getResponse(httpServletRequest,
                            data,
                            "The Admin Account was Successfully created, Check your Email to enable your Account",
                            HttpStatus.CREATED);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException | UserAlreadyExistsException e) {
            var response = getErrorResponse(httpServletRequest,
                    HttpStatus.EXPECTATION_FAILED,
                    e
            );
            return new ResponseEntity<>(response,HttpStatus.EXPECTATION_FAILED);
        }
        catch (Exception e) {
            var response = getErrorResponse(httpServletRequest,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e);
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("admins")
    public ResponseEntity<Response> getAdmins(HttpServletRequest httpServletRequest){
        try {
            var admins = adminService.getAllAdmins();

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
        } catch (Exception e) {
            var response = getErrorResponse(
                    httpServletRequest,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
