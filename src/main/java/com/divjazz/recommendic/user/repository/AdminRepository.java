package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUserPrincipal_Email(String email);

    boolean existsByUserPrincipal_Email(String email);
    boolean existsByUserId(String userId);

    Optional<Admin> findByUserId(String id);
}
