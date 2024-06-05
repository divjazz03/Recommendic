package com.divjazz.recommendic.recommendation.dto;

import com.divjazz.recommendic.recommendation.model.recommendationAttributes.RecommendationId;
import com.divjazz.recommendic.search.ConsultantSearchResult;
import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;
import com.divjazz.recommendic.user.dto.PatientInfoResponse;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.UserId;

public record RecommendationDTO(RecommendationId id, ConsultantInfoResponse consultant) {
}
