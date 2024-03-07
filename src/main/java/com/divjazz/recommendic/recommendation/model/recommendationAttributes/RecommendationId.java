package com.divjazz.recommendic.recommendation.model.recommendationAttributes;

import io.github.wimdeblauwe.jpearl.AbstractEntityId;

import java.util.UUID;

public class RecommendationId extends AbstractEntityId<UUID> {

    protected RecommendationId(){}

    public RecommendationId(UUID id){
        super(id);
    }
}
