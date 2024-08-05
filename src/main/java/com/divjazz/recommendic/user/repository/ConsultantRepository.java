package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {
    Optional<Consultant> findByEmail(String email);

    Optional<Consultant> findByUserId(UUID id);
    boolean existsByEmail(String email);
    Optional<List<Consultant>> findByMedicalCategory(MedicalCategory category);
}
