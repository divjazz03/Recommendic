package com.divjazz.recommendic.user.model.certification;

import io.github.wimdeblauwe.jpearl.AbstractEntityId;
import jakarta.persistence.Entity;

import java.util.UUID;


public class CertificationID extends AbstractEntityId<UUID>{
    protected CertificationID(){}
    public CertificationID(UUID id){
        super(id);
    }

}
