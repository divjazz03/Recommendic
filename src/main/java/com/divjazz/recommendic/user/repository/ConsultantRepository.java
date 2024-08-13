package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {
    Optional<Consultant> findByEmail(String email);

    Optional<Consultant> findByUserId(String id);
    boolean existsByEmail(String email);
    Optional<List<Consultant>> findByMedicalCategory(MedicalCategory category);

    @Query(value = """
            select * from consultant inner join users u on u.id = consultant.id where to_tsvector('english', specialization) @@ to_tsquery('english', :query)\s
            or to_tsvector('english', first_name) @@ to_tsquery('english',:query)
            """, nativeQuery = true)
    Set<Consultant> searchConsultant(String query);
}
