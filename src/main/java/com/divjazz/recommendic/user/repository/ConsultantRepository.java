package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {
    Optional<Consultant> findByEmail(String email);

    Optional<Consultant> findByUserId(String id);


    boolean existsByEmail(String email);
    Optional<List<Consultant>> findByMedicalCategory(MedicalCategory category);

    @Query(value = "select c from consultant c inner join users u on u.id = c.id where first_name = :name or last_name = :name", nativeQuery = true)
    Set<Consultant> findConsultantByName(@Param("name") String name);

    @Query(value = """
            select c from Consultant c inner join users u on u.id = c.id where certified = true and to_tsvector('english', specialization) @@ to_tsquery('english', :query)
            or to_tsvector('english', first_name) or to_tsvector('english', last_name) @@ to_tsquery('english',:query)
            """, nativeQuery = true)
    Set<Consultant> searchConsultant(@Param("query") String query);

    @Query(value = "select c from Consultation c where c.consultant_id = :consultantId", nativeQuery = true)
    Set<Consultation> findAllConsultationsByConsultantId(@Param("consultantId") String consultantId);

    @Query(value = "select c from Consultant c where c.certified = false", nativeQuery = true)
    Set<Consultant> findUnCertifiedConsultant();

}
