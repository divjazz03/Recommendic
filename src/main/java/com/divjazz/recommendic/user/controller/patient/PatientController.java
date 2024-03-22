package com.divjazz.recommendic.user.controller.patient;

import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.Gender;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.service.PatientService;
import com.divjazz.recommendic.utils.fileUpload.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ResponseMessage> createPatient(@RequestBody PatientRequestParams requestParams){
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
    public ResponseEntity<Set<Patient>> patients(){
        return patientService.getAllPatients();
    }

}
