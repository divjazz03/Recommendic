package com.divjazz.recommendic.user.controller.patient.payload;

import com.divjazz.recommendic.appointment.domain.Slot;
import com.divjazz.recommendic.user.dto.ConsultantEducationDTO;
import com.divjazz.recommendic.user.dto.ConsultantStatDTO;
import com.divjazz.recommendic.user.dto.ReviewDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Set;
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record PatientFullConsultantView(
        String id,
        String name,
        String title,
        double rating,
        int totalReviews,
        String bio,
        int experience,
        String location,
        String image,
        Set<String> specializations,
        Set<String> languages,
        int fee,
        Set<ConsultantEducationDTO> educations,
        ConsultantStatDTO stats,
        Set<Slot> availableSlots,
        Set<ReviewDTO> reviews,
        String profileImgUrl
) {
}
