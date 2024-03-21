package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.certification.CertificationID;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import io.github.wimdeblauwe.jpearl.UniqueIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserRepositoryImpl{

    private final UniqueIdGenerator<UUID> generator;


    public UserRepositoryImpl(UniqueIdGenerator<UUID> generator) {
        this.generator = generator;
    }


    public UserId nextId(){

        return new UserId(generator.getNextUniqueId());
    }

    public CertificationID nextCertificateId(){
        return new CertificationID(generator.getNextUniqueId());
    }

}
