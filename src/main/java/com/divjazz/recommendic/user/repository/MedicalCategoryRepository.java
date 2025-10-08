package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalCategoryRepository extends JpaRepository<MedicalCategoryEntity, Long> {

    Optional<MedicalCategoryEntity> findByName(String name);
}
