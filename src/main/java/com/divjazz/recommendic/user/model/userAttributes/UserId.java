package com.divjazz.recommendic.user.model.userAttributes;

import io.github.wimdeblauwe.jpearl.AbstractEntityId;

import java.util.UUID;

public class UserId extends AbstractEntityId<UUID> {

    public UserId(){}
    public UserId(UUID id){
        super(id);
    }
}
