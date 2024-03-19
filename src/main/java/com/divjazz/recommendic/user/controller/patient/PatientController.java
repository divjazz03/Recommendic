package com.divjazz.recommendic.user.controller.patient;

import com.divjazz.recommendic.user.UserType;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.service.AppUserDetailsService;
import com.divjazz.recommendic.user.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping("api/v1/patient")
public class PatientController {
    private final PatientService patientService;


    public PatientController(PatientService patientService) {
        this.patientService = patientService;

    }

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> createPatient(@RequestBody PatientRequestParams requestParams){
        PatientDTO patient = new PatientDTO(
                new UserName(requestParams.firstName(), requestParams.lastName()),
                requestParams.email(), requestParams.phoneNumber(),
                switch (requestParams.gender().toUpperCase()){
                    case "MALE" -> Gender.MALE;
                    case "FEMALE" -> Gender.FEMALE;
                    default -> throw new IllegalArgumentException("No Such Gender");
                },
                new Address(requestParams.zipCode(), requestParams.city(), requestParams.state(), requestParams.country()),
                requestParams.password()
        );
        return patientService.createPatient(patient);
    }

    @GetMapping("patients")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Set<User>> patients(){
        return patientService.getAllPatients();
    }

}
