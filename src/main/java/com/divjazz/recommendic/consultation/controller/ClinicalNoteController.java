package com.divjazz.recommendic.consultation.controller;

import com.divjazz.recommendic.consultation.dto.FinalizeNoteRequest;
import com.divjazz.recommendic.consultation.dto.NoteUpdateRequest;
import com.divjazz.recommendic.consultation.dto.NoteUpdateResponse;
import com.divjazz.recommendic.consultation.service.ClinicalNoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clinical-notes")
@RequiredArgsConstructor
public class ClinicalNoteController {
    private final ClinicalNoteService clinicalNoteService;
    @PostMapping
    public NoteUpdateResponse initOrUpdateNote(@RequestBody @Valid NoteUpdateRequest updateRequest) {
        return clinicalNoteService.updateNote(updateRequest);
    }
}
