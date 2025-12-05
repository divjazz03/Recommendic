package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface MedicalCategoryRepository extends JpaRepository<MedicalCategoryEntity, Long> {

    Optional<MedicalCategoryEntity> findByName(String name);

    Optional<MedicalCategoryEntity> findByMedicalCategoryId(String medicalCategoryId);
    Set<MedicalCategoryEntity> findAllByNameIn(Set<String> name);
    Set<MedicalCategoryEntity> findAllByMedicalCategoryIdIn(Set<String> medicalCategoryId);
}
