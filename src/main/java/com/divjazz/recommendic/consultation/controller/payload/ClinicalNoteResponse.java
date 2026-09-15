package com.divjazz.recommendic.consultation.controller.payload;

import com.divjazz.recommendic.consultation.enums.DocumentationStatus;
import com.divjazz.recommendic.consultation.model.*;
import lombok.Builder;

@Builder
public record ClinicalNoteResponse (
        String noteId,
        String consultationId,
        DocumentationStatus status,
        Integer version,
        String amendmentReason,
        Subjective subjective,
        Objective objective,
        Assessment assessment,
        QuickNote quickNote,
        Plan plan
) {
}
