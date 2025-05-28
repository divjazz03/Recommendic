package com.divjazz.recommendic.consultation.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.general.PageResponse;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.RequestUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController()
@RequestMapping("/api/v1/consultation")
@Tag(name = "Consultation API")
public class ConsultationController {


    private final ConsultationService consultationService;
    private final AuthUtils authUtils;

    public ConsultationController(ConsultationService consultationService,AuthUtils authUtils) {
        this.consultationService = consultationService;
        this.authUtils = authUtils;
    }

    @PostMapping("/request")
    @Operation(summary = "Request to open a consultation instance with a consultant")
    public ResponseEntity<Response<ConsultationResponse>> requestConsultation(@RequestParam("consultant_id") String consultantId) {
        var currentUser = authUtils.getCurrentUser();
        var result = consultationService.initializeConsultation(consultantId, currentUser);
        var response = RequestUtils.getResponse(result, "consultation initialized", HttpStatus.OK);
        return new ResponseEntity<>(response, response.status());
    }

    @GetMapping("/accept")
    @Operation(summary = "Accept the consultation request and start chatting")
    public ResponseEntity<Response<ConsultationResponse>> acceptConsultation(@RequestParam("consultation_id") String consultationId) {
        var result = consultationService.acknowledgeConsultation(consultationId);
        var response = RequestUtils.getResponse(result, "consultation acknowledged", HttpStatus.OK);
        return new ResponseEntity<>(response, response.status());
    }

    @GetMapping("/")
    @Operation(summary = "Get the current users consultation requests")
    public ResponseEntity<Response<PageResponse<ConsultationResponse>>> getConsultationRequest(@PageableDefault Pageable pageable){
        var result = consultationService.retrieveConsultationsOfConsultant(pageable);
        return ResponseEntity.ok(RequestUtils.getResponse(result, "success", HttpStatus.OK));
    }



}
