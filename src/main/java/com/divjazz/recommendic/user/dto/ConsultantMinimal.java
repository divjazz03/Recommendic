package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.appointment.controller.payload.ConsultationFee;

import java.util.List;

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
        String nextSlot
) {
}
