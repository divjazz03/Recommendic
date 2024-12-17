package com.divjazz.recommendic.user.controller.consultant;


import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.exception.NoSuchMedicalCategory;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.service.ConsultantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.divjazz.recommendic.utils.RequestUtils.getErrorResponse;
import static com.divjazz.recommendic.utils.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/consultant")
public class ConsultantController {

    private final ConsultantService consultantService;

    public ConsultantController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @PostMapping("create")
    public ResponseEntity<Response> createConsultant(@RequestBody @Valid ConsultantRegistrationParams requestParams, HttpServletRequest httpServletRequest){
        RequestContext.reset();
        RequestContext.setUserId(0L);
        try {
            ConsultantDTO consultantDTO = getConsultantDTO(requestParams);
            var consultantInfoResponse = consultantService.createConsultant(consultantDTO);
            return new ResponseEntity<>(getResponse(httpServletRequest,
                            Map.of("id", consultantInfoResponse.consultantId(),
                                    "last_name", consultantInfoResponse.lastName(),
                                    "first_name", consultantInfoResponse.firstName(),
                                    "gender", consultantInfoResponse.gender().toLowerCase(),
                                    "address", consultantInfoResponse.address(),
                                    "area_of_specialization", consultantInfoResponse.medicalSpecialization()),
                            "The Consultant was successfully created, Check your email to activate your account",
                            HttpStatus.CREATED
                            ), HttpStatus.CREATED);
        } catch (IllegalArgumentException|NoSuchMedicalCategory e) {
            var response = getErrorResponse(
                    httpServletRequest,
                    HttpStatus.EXPECTATION_FAILED,
                    e
            );
            return new ResponseEntity<>(response,HttpStatus.EXPECTATION_FAILED);
        } catch (Exception e){
            var response =  getErrorResponse(
                    httpServletRequest,
                    HttpStatus.EXPECTATION_FAILED,
                    e
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    private static ConsultantDTO getConsultantDTO(@NotNull ConsultantRegistrationParams requestParams) {
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
        MedicalCategory medicalCategory = switch (requestParams.medicalSpecialization().toUpperCase()){
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
        return new ConsultantDTO(userName,userEmail,phoneNumber,gender,address, requestParams.password(), medicalCategory);
    }

    @GetMapping("consultants")
    public ResponseEntity<Response> getConsultants(HttpServletRequest httpServletRequest ){
        try {
            var data = consultantService.getAllConsultants().stream()
                    .map(consultant -> new ConsultantInfoResponse(
                            consultant.getUserId(),
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
            return new ResponseEntity<>(response,HttpStatus.FOUND);
        } catch (Exception e) {
            var response =  getErrorResponse(
                    httpServletRequest,
                    HttpStatus.EXPECTATION_FAILED,
                    e
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
