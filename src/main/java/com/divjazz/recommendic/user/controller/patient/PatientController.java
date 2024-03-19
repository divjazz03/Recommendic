package com.divjazz.recommendic.user.controller;

import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/v1/patient")
@Controller
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Patient> createPatient(PatientRequestParams requestParams){
        UserName userName = new UserName(requestParams.firstName(), requestParams.lastName());
        Email email = new Email(requestParams.email());
        PhoneNumber phoneNumber = new PhoneNumber(requestParams.phoneNumber());
        Gender gender = Gender.valueOf(requestParams.gender());
        Address address = new Address(requestParams.zipCode(),
                requestParams.city(),
                requestParams.state(),
                requestParams.country());

        PatientDTO patient = new PatientDTO(userName,email,phoneNumber,gender,address);

        return patientService.createPatient(patient);
    }
}
