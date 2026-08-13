package com.divjazz.recommendic.consultation.dto;

import com.divjazz.recommendic.consultation.enums.DocumentationStatus;
import com.divjazz.recommendic.consultation.model.Assessment;
import com.divjazz.recommendic.consultation.model.Objective;
import com.divjazz.recommendic.consultation.model.Plan;
import com.divjazz.recommendic.consultation.model.Subjective;

public record NoteUpdateResponse(
    String noteId,
    DocumentationStatus status,
    Integer version,
    String amendmentReason,
    Subjective subjective,
    Objective objective,
    Assessment assessment,
    Plan plan
) {
}
