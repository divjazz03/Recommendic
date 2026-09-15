package com.divjazz.recommendic.consultation.service;

import com.divjazz.recommendic.consultation.controller.payload.ClinicalNoteResponse;
import com.divjazz.recommendic.consultation.dto.NoteUpdateRequest;
import com.divjazz.recommendic.consultation.dto.NoteUpdateResponse;
import com.divjazz.recommendic.consultation.enums.DocumentationStatus;
import com.divjazz.recommendic.consultation.mapper.ClinicalNoteMapper;
import com.divjazz.recommendic.consultation.model.ClinicalNote;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.repository.ClinicalNoteRepository;
import com.divjazz.recommendic.global.exception.AppBadRequestException;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClinicalNoteService {
    private final ConsultationService consultationService;
    private final ClinicalNoteRepository clinicalNoteRepository;
    private final ClinicalNoteMapper clinicalNoteMapper;
    @Transactional
    public NoteUpdateResponse updateNote(NoteUpdateRequest noteUpdateRequest) {
        Consultation consultation = consultationService.getConsultationById(noteUpdateRequest.consultationId());
        ClinicalNote clinicalNote = null;
        if (Objects.isNull(noteUpdateRequest.noteId())) {
            clinicalNote = ClinicalNote.builder()
                    .consultation(consultation)
                    .patientId(consultation.getAppointment().getPatient().getUserId())
                    .consultantId(consultation.getAppointment().getConsultant().getUserId())
                    .version(1)
                    .quickNote(noteUpdateRequest.quickNote())
                    .assessment(noteUpdateRequest.assessment())
                    .subjective(noteUpdateRequest.subjective())
                    .objective(noteUpdateRequest.objective())
                    .lastEditedBy(consultation.getAppointment().getConsultant().getUserId())
                    .build();
        } else {
            clinicalNote = clinicalNoteRepository.findByNoteId(noteUpdateRequest.noteId())
                    .orElseThrow(() -> new EntityNotFoundException("Clinical note with provided id not found"));

            if (clinicalNote.getStatus() == DocumentationStatus.SIGNED) {
                throw new AppBadRequestException("This Clinical document has already been finalize. Request an amendment");
            }

            updateClinicalNote(noteUpdateRequest, clinicalNote);
        }

        clinicalNote = clinicalNoteRepository.save(clinicalNote);
        return clinicalNoteMapper.toNoteUpdateResponse(clinicalNote);
    }

    public void finalizeNote(ClinicalNote note) {
        note.setStatus(DocumentationStatus.SIGNED);
        note = clinicalNoteRepository.save(note);
        clinicalNoteMapper.toNoteUpdateResponse(note);
    }
    @Transactional
    public ClinicalNoteResponse getLatestClinicalNoteByConsultationId (String consultationId) {
        ClinicalNote note = getNoteByConsultationId(consultationId);

        return ClinicalNoteResponse.builder()
                .noteId(note.getNoteId())
                .quickNote(note.getQuickNote())
                .status(note.getStatus())
                .amendmentReason(note.getAmendmentReason())
                .consultationId(note.getConsultation().getConsultationId())
                .plan(note.getPlan())
                .objective(note.getObjective())
                .subjective(note.getSubjective())
                .assessment(note.getAssessment())
                .version(note.getVersion())
                .build();
    }

    public ClinicalNote getNoteByConsultationId (String consultationId) {
        return clinicalNoteRepository.findTop1ByConsultation_ConsultationIdOrderByVersionDesc(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("No Clinical note for provided consultationId"));
    }

    private void updateClinicalNote(NoteUpdateRequest noteUpdateRequest, ClinicalNote clinicalNoteToUpdate) {
        if (Objects.nonNull(noteUpdateRequest.objective())) {
            clinicalNoteToUpdate.setObjective(noteUpdateRequest.objective());
        }
        if (Objects.nonNull(noteUpdateRequest.subjective())) {
            clinicalNoteToUpdate.setSubjective(noteUpdateRequest.subjective());
        }
        if (Objects.nonNull(noteUpdateRequest.assessment())) {
            clinicalNoteToUpdate.setAssessment(noteUpdateRequest.assessment());
        }
        if (Objects.nonNull(noteUpdateRequest.plan())) {
            clinicalNoteToUpdate.setPlan(noteUpdateRequest.plan());
        }
        if (Objects.nonNull(noteUpdateRequest.quickNote())) {
            clinicalNoteToUpdate.setQuickNote(noteUpdateRequest.quickNote());
        }
    }
}
