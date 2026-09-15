package com.divjazz.recommendic.consultation.dto;

import com.divjazz.recommendic.consultation.enums.DocumentationStatus;
import com.divjazz.recommendic.consultation.model.*;

public record NoteUpdateResponse(
    String noteId,
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
