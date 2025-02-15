package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface UserBaseRepository <T extends User> extends JpaRepository<T, Long> {

    Optional<T> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<T> findByUserId(String id);

    void deleteByUserId(String userId);
}
