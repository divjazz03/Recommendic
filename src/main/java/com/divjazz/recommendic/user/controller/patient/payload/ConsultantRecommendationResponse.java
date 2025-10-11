package com.divjazz.recommendic.user.controller.patient.payload;

import com.divjazz.recommendic.appointment.controller.payload.ConsultantSchedulesResponse;
import com.divjazz.recommendic.appointment.controller.payload.ConsultationFee;

import java.util.List;
import java.util.Set;

public record ConsultantRecommendationResponse(
    Set<ConsultantMinimal> consultants
) {
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

    ){}
}
