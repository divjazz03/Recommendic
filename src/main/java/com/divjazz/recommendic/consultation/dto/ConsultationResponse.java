package com.divjazz.recommendic.consultation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@JsonInclude(value = NON_DEFAULT)
public record ConsultationResponse(
        String summary,
        String start_time,
        String patientName,
        String consultantName,
        Long consultationId,
        String status,
        String channel
) implements Serializable {
}
