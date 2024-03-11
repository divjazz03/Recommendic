package com.divjazz.recommendic.user.model.certification;

import io.github.wimdeblauwe.jpearl.AbstractEntityId;

import java.util.UUID;

public class CertificationID extends AbstractEntityId<UUID> {
    public CertificationID(){}
    public CertificationID(UUID id){
        super(id);
    }
}