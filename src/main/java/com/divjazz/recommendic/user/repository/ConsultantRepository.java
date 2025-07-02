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

    List<Consultant> findByMedicalCategoryIgnoreCase(String medicalCategory);
    Optional<Consultant> findByUserId(String userId);
    Optional<Consultant> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByUserId(String userId);

    @Query(value = "select * from consultant c where c.username ->> 'first_name' = :name or c.username ->> 'last_name' = :name " , nativeQuery = true)
    Set<Consultant> findConsultantByName(@Param("name") String name);

    @Query(value = """
            select * from consultant c where certified = true and c.search_vector @@ to_tsquery('english',:query)
            """, nativeQuery = true)
    Set<Consultant> searchConsultant(@Param("query") String query);

    @Query(value = "select * from consultant  c where c.certified = false", nativeQuery = true)
    Set<Consultant> findUnCertifiedConsultant();

}
