package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.projection.ConsultantInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {

    Set<Consultant> findByMedicalCategoryIgnoreCase(String medicalCategory);

    Optional<Consultant> findByUserId(String userId);

    Optional<Consultant> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByUserId(String userId);

    void deleteByUserId(String userId);


    @Query(value = """ 
            SELECT c FROM Consultant c
            where c.certified = false""")
    Set<Consultant> findUnCertifiedConsultant();

    @Query(value = """
            select c.user_id as consultantId,
            cp.username ->> 'lastname' as lastName,
            cp.username ->> 'firstname' as firstName,
            c.gender as gender,
            cp.phone_number as phoneNumber,
            cp.address as address,
            c.specialization as medicalSpecialization
            from consultant c
            left join consultant_stat cs on c.id = cs.id
            left join consultant_profiles cp on c.id = cp.id
            where certified = true and c.search_vector @@ to_tsquery('english',:query)
            """, nativeQuery = true)
    Set<ConsultantInfoProjection> findConsultantInfo(@Param("query") String query);

}
