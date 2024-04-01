package com.divjazz.recommendic.recommendation.dto;

import com.divjazz.recommendic.recommendation.model.recommendationAttributes.RecommendationId;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.UserId;

public record RecommendationDTO(RecommendationId id, Consultant consultant, Patient patient) {
}
