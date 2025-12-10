package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {


    Optional<Patient> findByUserId(String userId);

    Optional<Patient> findByUserPrincipal_Email(String email);

    boolean existsByUserPrincipal_Email(String email);

    boolean existsByUserId(String userId);




    void deleteByUserId(String userId);

    @Query("""
        SELECT
                p.id as id,
                p.userId as userId,
                p.gender as gender,
                p.lastLogin as lastLogin,
                p.userType as userType,
                p.userStage as userStage,
                p.userPrincipal as userPrincipal
        FROM Patient p
        WHERE p.userId = :userId
        """)
    UserProjection findByUserIdReturningProjection(String userId);

@Query("""
        SELECT
                p.id as id,
                p.userId as userId,
                p.gender as gender,
                p.lastLogin as lastLogin,
                p.userType as userType,
                p.userStage as userStage,
                p.userPrincipal as userPrincipal
        FROM Patient p
        WHERE p.userPrincipal.email = :email
        """)
    UserProjection findByEmailReturningProjection(String email);

}
