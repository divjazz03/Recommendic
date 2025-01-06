package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.Patient;


import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByUserId(String userId);
    Optional<Patient> findByUserId(String id);

    @Query(value = "select * from consultation where patient_id=:patientId", nativeQuery = true)
    Set<Consultation> findAllConsultationsByPatientId(String patientId);

    Set<Patient> findPatientByMedicalCategories(Set<MedicalCategory> category);

}
