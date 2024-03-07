package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.userAttributes.UserId;
import org.springframework.stereotype.Repository;


public interface UserRepositoryCustom {

    UserId nextId();
}
