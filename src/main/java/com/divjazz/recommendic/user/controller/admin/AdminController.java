package com.divjazz.recommendic.user.controller.admin;


import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.service.AdminService;
import java.util.Set;

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
    public ResponseEntity<AdminResponse> createAdmin(@RequestBody AdminRegistrationParams requestParams){
        AdminDTO adminDTO = new AdminDTO(
                new UserName(requestParams.firstName(), requestParams.lastName()), requestParams.email(), requestParams.phoneNumber(),
                switch (requestParams.gender().toUpperCase()){
                    case "MALE" -> Gender.MALE;
                    case "FEMALE" -> Gender.FEMALE;
                    default -> throw new IllegalArgumentException("No such Gender");
                },
                new Address(requestParams.zipcode(), requestParams.city(), requestParams.state(), requestParams.country())
        );
        return adminService.createAdmin(adminDTO);
    }

    @GetMapping("patients")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Set<Patient>> patients(){
        return new ResponseEntity<>(patientService.getAllPatients(), HttpStatus.OK);
    }

    @GetMapping("admins")
    public ResponseEntity<Set<Admin>> getAdmins(){
        return adminService.getAllAdmins();
    }
}
