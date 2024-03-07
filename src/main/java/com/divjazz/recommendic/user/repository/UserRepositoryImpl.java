package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.userAttributes.UserId;
import io.github.wimdeblauwe.jpearl.UniqueIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final UniqueIdGenerator<UUID> generator ;


    public UserRepositoryImpl(UniqueIdGenerator<UUID> generator) {
        this.generator = generator;
    }


    @Override
    public UserId nextId() {
        return new UserId(generator.getNextUniqueId());
    }
}
