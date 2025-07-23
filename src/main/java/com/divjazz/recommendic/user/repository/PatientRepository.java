package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface PatientRepository extends JpaRepository<Patient, Long> {


    Set<Patient> findPatientByMedicalCategories(String[] category);

    Optional<Patient> findByUserId(String userId);
    Optional<Patient> findByUserPrincipal_Email(String email);
    boolean existsByUserPrincipal_Email(String email);
    boolean existsByUserId(String userId);


    void deleteByUserId(String userId);
    ;

}
