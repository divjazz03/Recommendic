package com.divjazz.recommendic.user.controller.consultant;


import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.service.ConsultantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/consultant")
public class ConsultantController {

    private final ConsultantService consultantService;

    public ConsultantController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Consultant> createConsultant(@RequestParam ConsultantRequestParams requestParams){
        UserName userName = new UserName(requestParams.firstName(), requestParams.lastName());
        Email userEmail = new Email(requestParams.email());
        PhoneNumber phoneNumber = new PhoneNumber(requestParams.phoneNumber());
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
}
