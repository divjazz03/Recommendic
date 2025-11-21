package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.repository.projection.ConsultantInfoProjection;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {

    Set<Consultant> findBySpecialization(MedicalCategoryEntity specialization);

    Optional<Consultant> findByUserId(String userId);

    Optional<Consultant> findByUserPrincipal_Email(String email);

    boolean existsByUserPrincipal_Email(String email);

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
            where certified = true
            """, nativeQuery = true)
    Set<ConsultantInfoProjection> findConsultantInfo(@Param("query") String query);

@Query("""
        SELECT
                c.id as id,
                c.userId as userId,
                c.gender as gender,
                c.lastLogin as lastLogin,
                c.userType as userType,
                c.userStage as userStage,
                c.userPrincipal as userPrincipal
        FROM Consultant c
        WHERE c.userPrincipal.email = :email
        """)
    UserProjection findByEmailReturningProjection(String email);

}
