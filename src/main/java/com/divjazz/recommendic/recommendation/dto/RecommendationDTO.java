package com.divjazz.recommendic.recommendation.dto;


import com.divjazz.recommendic.user.controller.consultant.payload.ConsultantInfoResponse;

import java.util.UUID;

public record RecommendationDTO(UUID id, ConsultantInfoResponse consultant) {
}
