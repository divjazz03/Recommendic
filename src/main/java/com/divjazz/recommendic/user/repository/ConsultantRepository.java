package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {

    Optional<List<Consultant>> findByMedicalCategory(String medicalCategory);
    Optional<Consultant> findByUserId(String userId);
    Optional<Consultant> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByUserId(String userId);

    @Query(value = "select c from consultant c where first_name = :name or last_name = :name and dtype = 'Consultant'" , nativeQuery = true)
    Set<Consultant> findConsultantByName(@Param("name") String name);

    @Query(value = """
            select c from consultant c where certified = true and dtype = 'Consultant' and to_tsvector('english', specialization) @@ to_tsquery('english', :query)
            or to_tsvector('english', first_name) or to_tsvector('english', last_name) @@ to_tsquery('english',:query)
            """, nativeQuery = true)
    Set<Consultant> searchConsultant(@Param("query") String query);

    @Query(value = "select c from consultant  c where dtype = 'Consultant' and c.certified = false", nativeQuery = true)
    Set<Consultant> findUnCertifiedConsultant();

}
