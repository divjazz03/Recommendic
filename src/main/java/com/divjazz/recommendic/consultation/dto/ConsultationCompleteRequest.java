package com.divjazz.recommendic.consultation.dto;

import jakarta.validation.constraints.NotNull;

public record ConsultationCompleteRequest(
        @NotNull
        String consultationId
        ) {
}
