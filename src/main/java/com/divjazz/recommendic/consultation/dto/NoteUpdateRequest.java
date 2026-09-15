package com.divjazz.recommendic.consultation.dto;

import com.divjazz.recommendic.consultation.model.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record NoteUpdateRequest(
        @NotBlank(message = "consultationId is required") String consultationId,
        String noteId,
        QuickNote quickNote,
        Subjective subjective,
        Objective objective,
        Assessment assessment,
        Plan plan
) {
}
