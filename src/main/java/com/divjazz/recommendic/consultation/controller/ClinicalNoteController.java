package com.divjazz.recommendic.consultation.controller;

import com.divjazz.recommendic.consultation.controller.payload.ClinicalNoteResponse;
import com.divjazz.recommendic.consultation.dto.FinalizeNoteRequest;
import com.divjazz.recommendic.consultation.dto.NoteUpdateRequest;
import com.divjazz.recommendic.consultation.dto.NoteUpdateResponse;
import com.divjazz.recommendic.consultation.service.ClinicalNoteService;
import com.divjazz.recommendic.global.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/clinical-notes")
@RequiredArgsConstructor
public class ClinicalNoteController {
    private final ClinicalNoteService clinicalNoteService;
    @PostMapping
    public Response<NoteUpdateResponse> initOrUpdateNote(@RequestBody @Valid NoteUpdateRequest updateRequest) {
        return getResponse(clinicalNoteService.updateNote(updateRequest), HttpStatus.OK);
    }
    @GetMapping("/consultation/{consultationId}")
    public Response<ClinicalNoteResponse> getLatestClinicalNoteByConsultationId(@PathVariable String consultationId) {
        ClinicalNoteResponse result = clinicalNoteService.getLatestClinicalNoteByConsultationId(consultationId);
        return getResponse(result, HttpStatus.OK);
    }
}
