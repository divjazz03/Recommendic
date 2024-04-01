package com.divjazz.recommendic.user.controller.consultant;


import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.exceptions.NoSuchMedicalCategory;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.utils.fileUpload.ResponseMessage;
import com.github.javafaker.Medical;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/consultant")
public class ConsultantController {

    private final ConsultantService consultantService;

    public ConsultantController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseMessage> createConsultant(@RequestBody ConsultantRegistrationParams requestParams){
        UserName userName = new UserName(requestParams.firstName(), requestParams.lastName());
        String userEmail = requestParams.email();
        String phoneNumber = requestParams.phoneNumber();
        Gender gender = switch (requestParams.gender().toUpperCase()){
          case "MALE" -> Gender.MALE;
          case "FEMALE" -> Gender.FEMALE;
            default -> throw new IllegalArgumentException(String.format("Gender %s is not valid", requestParams.gender()));
        };
        Address address = new Address(requestParams.zipCode(),
                requestParams.city(),
                requestParams.state(),
                requestParams.country());
        MedicalCategory medicalCategory = switch (requestParams.medicalCategory().toUpperCase()){
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
        ConsultantDTO consultantDTO = new ConsultantDTO(userName,userEmail,phoneNumber,gender,address, requestParams.password(), medicalCategory);
        return consultantService.createConsultant(consultantDTO);

    }

    @GetMapping("consultants")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Set<Consultant>> getConsultants(){
        return consultantService.getAllConsultants();
    }
}
