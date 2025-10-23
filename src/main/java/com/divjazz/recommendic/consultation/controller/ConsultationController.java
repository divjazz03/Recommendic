package com.divjazz.recommendic.consultation.controller;

import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.consultation.dto.ConsultationCompleteRequest;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.divjazz.recommendic.global.RequestUtils.*;

@RestController()
@RequestMapping("/api/v1/consultations")
@Tag(name = "Consultation API")
@RequiredArgsConstructor
public class ConsultationController {


    private final ConsultationService consultationService;

    @PostMapping(value = "/{id}/start")
    @Operation(summary = "Starts a Consultation Session")
    public ResponseEntity<Response<ConsultationResponse>> start(@PathVariable String id) {
        ConsultationResponse response = consultationService.startConsultation(id);
        return ResponseEntity.created(URI.create("/start/"+ id))
                .body(getResponse(response, HttpStatus.CREATED));
    }

    @PostMapping(value = "/{id}/complete")
    @Operation(summary = "Finalizes a Consultation Session")
    public ResponseEntity<Response<ConsultationResponse>> complete(@PathVariable String id, @RequestBody ConsultationCompleteRequest request) {
        ConsultationResponse response = consultationService.completeConsultation(id, request.summary());
        return ResponseEntity.ok(getResponse(response, HttpStatus.OK
        ));
    }


}
