package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.appointment.controller.payload.ConsultationFee;
import com.divjazz.recommendic.appointment.domain.Slot;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record ConsultantMinimal(
        String id,
        String name,
        String specialty,
        double rating,
        int reviews,
        int experience,
        String location,
        String availability,
        ConsultationFee fee,
        String image,
        List<String> qualifications,
        List<String> languages,
        Slot nextSlot
) {
}
