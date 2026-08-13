package com.divjazz.recommendic.consultation.dto;

import com.divjazz.recommendic.consultation.model.Assessment;
import com.divjazz.recommendic.consultation.model.Objective;
import com.divjazz.recommendic.consultation.model.Plan;
import com.divjazz.recommendic.consultation.model.Subjective;
import jakarta.validation.constraints.NotBlank;

public record NoteUpdateRequest(
        @NotBlank(message = "consultationId is required") String consultationId,
        String noteId,
        Subjective subjective,
        Objective objective,
        Assessment assessment,
        Plan plan
) {
}
