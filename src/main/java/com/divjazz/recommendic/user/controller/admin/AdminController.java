package com.divjazz.recommendic.user.controller.admin;


import com.divjazz.recommendic.user.domain.Response;
import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.service.AdminService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.divjazz.recommendic.user.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")

public class AdminController {

    private final AdminService adminService;
    private final PatientService patientService;

    public AdminController(AdminService adminService, PatientService patientService) {
        this.adminService = adminService;
        this.patientService = patientService;
    }

    @PostMapping("create")
    public ResponseEntity<Response> createAdmin(@RequestBody AdminRegistrationParams requestParams){
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

            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.CREATED.value(),
                    "",
                    HttpStatus.CREATED,
                    "The Admin Account was Successfully created",
                    "",
                    Map.of(
                            "email",adminResponse.email(),
                            "password",adminResponse.password(),
                            "dateOfExpiry",adminResponse.dateOfExpiry().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    );

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "",
                    HttpStatus.EXPECTATION_FAILED,
                    e.getMessage(),
                    e.getClass().getName(),
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.EXPECTATION_FAILED);
        }
        catch (Exception e) {
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    e.getClass().getName(),
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("patients")
    public ResponseEntity<Response> patients(){
        try {
            var data = patientService.getAllPatients();
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.OK.value(),
                    "",
                    HttpStatus.OK,
                    "Success in retrieving the Patient Users",
                    "",
                    Map.of("patients", data));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    e.getClass().getName(),
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("admins")
    public ResponseEntity<Response> getAdmins(){
        try {
            var data = adminService.getAllAdmins();
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.OK.value(),
                    "",
                    HttpStatus.OK,
                    "Success in retrieving the Admin Users",
                    "",
                    Map.of("admins", data));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    e.getClass().getName(),
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
