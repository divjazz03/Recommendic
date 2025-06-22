package com.divjazz.recommendic.consultation.controller;

import com.divjazz.recommendic.RequestUtils;
import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.consultation.dto.ConsultationCompleteRequest;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.security.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.divjazz.recommendic.RequestUtils.*;

@RestController()
@RequestMapping("/api/v1/consultation")
@Tag(name = "Consultation API")
@RequiredArgsConstructor
public class ConsultationController {


    private final ConsultationService consultationService;

    @PostMapping(value = "/start/{appointment_id}")
    @Operation(summary = "Starts a Consultation Session")
    public ResponseEntity<Response<ConsultationResponse>> start(@PathVariable("appointment_id") Long appointmentId) {
        ConsultationResponse response = consultationService.startConsultation(appointmentId);
        return ResponseEntity.created(URI.create("/start/"+appointmentId))
                .body(getResponse(response,"success", HttpStatus.CREATED));
    }

    @PostMapping(value = "/complete/{id}")
    @Operation(summary = "Finalizes a Consultation Session")
    public ResponseEntity<Response<ConsultationResponse>> complete(@PathVariable("id") Long id, @RequestBody ConsultationCompleteRequest request) {
        ConsultationResponse response = consultationService.completeConsultation(id, request.summary());
        return ResponseEntity.ok(getResponse(response,"success", HttpStatus.CREATED));
    }


}
