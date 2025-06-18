package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface PatientRepository extends JpaRepository<Patient, Long> {


    Set<Patient> findPatientByMedicalCategories(String[] category);

    Optional<Patient> findByUserId(String userId);
    Optional<Patient> findByEmail(String email);
    boolean existsByEmail(String email);

    void deleteByUserId(String userId);

    @Query(value = "select * from consultation where patient_id=:patientId", nativeQuery = true)
    Set<Consultation> findAllConsultationByPatientId(String patientId);

}
