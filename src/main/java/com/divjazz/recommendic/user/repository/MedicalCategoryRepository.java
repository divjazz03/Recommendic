package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MedicalCategoryRepository extends JpaRepository<MedicalCategoryEntity, Long> {

    Optional<MedicalCategoryEntity> findByName(String name);
    Set<MedicalCategoryEntity> findAllByNameIn(List<String> name);
}
