package com.divjazz.recommendic.consultation.controller;

import com.divjazz.recommendic.consultation.controller.payload.CheckInResponse;
import com.divjazz.recommendic.consultation.controller.payload.JoinResponse;
import com.divjazz.recommendic.consultation.controller.payload.StartResponse;
import com.divjazz.recommendic.consultation.dto.ConsultationResponseMinimal;
import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.consultation.dto.ConsultationCompleteRequest;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.global.general.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.divjazz.recommendic.global.RequestUtils.*;

@RestController()
@RequestMapping("/api/v1/consultations")
@Tag(name = "Consultation API")
@RequiredArgsConstructor
public class ConsultationController {


    private final ConsultationService consultationService;


    @PostMapping(value = "/{id}/start")
    @Operation(summary = "Starts a Consultation Session")
    public Response<StartResponse> start(@PathVariable String id) {
        var response = consultationService.startConsultation(id);
        return getResponse(response, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/join")
    public Response<JoinResponse> join (@PathVariable String id) {
        var result = consultationService.joinConsultation(id);
        return getResponse(result, HttpStatus.OK);
    }
    @PostMapping(value = "/{id}/check-in")
    public Response<CheckInResponse> checkIn (@PathVariable String id) {
        var result = consultationService.checkIn(id);
        return getResponse(result, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public Response<ConsultationResponse> getConsultationDetails (@PathVariable String id) {
        var result = consultationService.getConsultationByConsultationId(id);
        return getResponse(result, HttpStatus.OK);
    }

    @GetMapping
    public Response<PageResponse<ConsultationResponse>> getAllConsultationDetails (@PageableDefault Pageable pageable) {
        var result = consultationService.retrieveConsultationPaged(pageable);
        return getResponse(result, HttpStatus.OK);
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
