package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
