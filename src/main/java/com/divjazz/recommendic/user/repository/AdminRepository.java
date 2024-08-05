package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Admin> findByUserId(String id);
}
