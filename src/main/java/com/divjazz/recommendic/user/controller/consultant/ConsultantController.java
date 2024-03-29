package com.divjazz.recommendic.user.controller.consultant;


import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.utils.fileUpload.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        ConsultantDTO consultantDTO = new ConsultantDTO(userName,userEmail,phoneNumber,gender,address, requestParams.password());
        return consultantService.createConsultant(consultantDTO);

    }

    @GetMapping("consultants")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Set<Consultant>> getConsultants(){
        return consultantService.getAllConsultants();
    }
}
