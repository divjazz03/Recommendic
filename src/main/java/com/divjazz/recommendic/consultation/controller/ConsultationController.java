package com.divjazz.recommendic.consultation.controller;

import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.consultation.dto.ConsultationCompleteRequest;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @PostMapping(value = "/{id}/start/{time}")
    @Operation(summary = "Starts a Consultation Session")
    public ResponseEntity<Response<ConsultationResponse>> start(@PathVariable String id, @PathVariable String time) {
        ConsultationResponse response = consultationService.startConsultation(id, time);
        return ResponseEntity.status(HttpStatus.OK)
                .body(getResponse(response, HttpStatus.OK));
    }

    @PostMapping(value = "/complete")
    @Operation(summary = "Finalizes a Consultation Session")
    public ResponseEntity<Response<ConsultationResponse>> complete( @Valid @RequestBody ConsultationCompleteRequest request) {
        ConsultationResponse response = consultationService.completeConsultation(
                request
        );
        return ResponseEntity.ok(getResponse(response, HttpStatus.OK
        ));
    }


}
