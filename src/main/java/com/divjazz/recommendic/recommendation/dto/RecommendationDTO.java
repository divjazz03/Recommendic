package com.divjazz.recommendic.recommendation.dto;


import com.divjazz.recommendic.user.dto.ConsultantResponse;

import java.util.UUID;

public record RecommendationDTO(UUID id, ConsultantResponse consultant) {
}
