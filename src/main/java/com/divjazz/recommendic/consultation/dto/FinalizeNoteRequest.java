package com.divjazz.recommendic.consultation.dto;

import jakarta.validation.constraints.NotBlank;

public record FinalizeNoteRequest(@NotBlank(message = "noteId is required") String noteId) {
}
