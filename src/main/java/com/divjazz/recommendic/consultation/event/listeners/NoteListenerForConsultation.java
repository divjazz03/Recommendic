package com.divjazz.recommendic.consultation.event.listeners;

import com.divjazz.recommendic.consultation.event.ConsultationEndedWithoutFollowUpData;
import com.divjazz.recommendic.consultation.event.ConsultationEvent;
import com.divjazz.recommendic.consultation.event.ConsultationEventData;
import com.divjazz.recommendic.consultation.service.ClinicalNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoteListenerForConsultation {
    private final ClinicalNoteService clinicalNoteService;
    @EventListener
    void onConsultationEvent (ConsultationEventData eventData) {
        if (eventData.getEvent() == ConsultationEvent.CONSULTATION_ENDED_WITHOUT_FOLLOWUP) {
            var data = (ConsultationEndedWithoutFollowUpData) eventData;
            handleNoteFinalization(data.consultationId());
        } else if (eventData.getEvent() == ConsultationEvent.CONSULTATION_ENDED_WITH_FOLLOWUP) {
            var data = (ConsultationEndedWithoutFollowUpData) eventData;
            handleNoteFinalization(data.consultationId());
        }
    }

    private void handleNoteFinalization (String consultationId) {
        var clinicalNote = clinicalNoteService.getNoteByConsultationId(consultationId);
        clinicalNoteService.finalizeNote(clinicalNote);
    }
}
