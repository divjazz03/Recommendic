package com.divjazz.recommendic.user.model.certification;

import io.github.wimdeblauwe.jpearl.AbstractEntityId;
import jakarta.persistence.Entity;

import java.util.UUID;


public class CertificationID extends AbstractEntityId<UUID>{
    public CertificationID(UUID id){
        super(id);
    }

}
