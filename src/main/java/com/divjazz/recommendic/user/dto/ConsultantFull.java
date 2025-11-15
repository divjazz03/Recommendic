package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.appointment.controller.payload.ConsultationFee;
import com.divjazz.recommendic.appointment.domain.Slot;
import lombok.Builder;

import java.util.Set;

@Builder
public record ConsultantFull(
        String id,
        String name,
        Set<String> specializations,
        double rating,
        int totalReviews,
        String bio,
        String title,
        int experience,
        String location,
        ConsultationFee fee,
        String image,
        Set<String> languages,
        Set<Slot> availableSlots,
        Set<ConsultantEducationDTO> educations,
        ConsultantStatDTO stats,
        Set<ReviewDTO> reviews,
        String profileImgUrl
) {
}
