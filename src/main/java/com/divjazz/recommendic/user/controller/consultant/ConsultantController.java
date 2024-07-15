package com.divjazz.recommendic.user.controller.consultant;


import com.divjazz.recommendic.user.domain.Response;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.exceptions.NoSuchMedicalCategory;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.utils.fileUpload.FileResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
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
    public ResponseEntity<Response> createConsultant(@RequestBody ConsultantRegistrationParams requestParams){
        try {
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
            var fileResponse = consultantService.createConsultant(consultantDTO);
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.CREATED.value(),
                    "",
                    HttpStatus.CREATED,
                    "The Consultant was successfully created",
                    "",
                    Map.of("id", fileResponse.consultantId(),
                            "last_name", fileResponse.lastName(),
                            "first_name", fileResponse.firstName(),
                            "gender", fileResponse.gender().toLowerCase(),
                            "address", fileResponse.address(),
                            "area_of_specialization", fileResponse.medicalSpecialization())
            );
            return new ResponseEntity<>(response,HttpStatus.EXPECTATION_FAILED);
        } catch (IllegalArgumentException|NoSuchMedicalCategory e) {
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "",
                    HttpStatus.EXPECTATION_FAILED,
                    e.getMessage(),
                    e.getClass().getName(),
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.EXPECTATION_FAILED);
        } catch (Exception e){
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

    @GetMapping("consultants")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Response> getConsultants(){
        try {
            var data = consultantService.getAllConsultants().stream()
                    .map(consultant -> new ConsultantInfoResponse(
                            consultant.getReferenceId().toString(),
                            consultant.getUserNameObject().getLastName(),
                            consultant.getUserNameObject().getFirstName(),
                            consultant.getGender().toString().toLowerCase(),
                            consultant.getAddress(),
                            consultant.getMedicalCategory().toString().toLowerCase()
                    ));
            var response = new Response(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.OK.value(),
                    "",
                    HttpStatus.OK,
                    "All Consultants have been successfully retrieved",
                    "",
                    Map.of("consultants",data)
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
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
