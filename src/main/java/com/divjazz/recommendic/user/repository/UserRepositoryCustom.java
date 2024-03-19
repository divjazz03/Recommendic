package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.UserType;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepositoryCustom extends JpaRepository<User, UserId> {

    Optional<User> findByUserTypeAndEmail(UserType userType, String email);
    Optional<Set<User>> findAllByUserType(UserType userType);
    Optional<User> findUserByEmail(String email);

    UserId nextId();
}
