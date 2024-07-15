package com.divjazz.recommendic.recommendation.dto;


import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;

import java.util.UUID;

public record RecommendationDTO(UUID id, ConsultantInfoResponse consultant) {
}
