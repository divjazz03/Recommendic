package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.Patient;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<Patient> findByUserId(String id);

}
