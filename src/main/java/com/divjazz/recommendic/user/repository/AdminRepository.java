package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUserPrincipal_Email(String email);

    boolean existsByUserPrincipal_Email(String email);
    boolean existsByUserId(String userId);

    Optional<Admin> findByUserId(String id);

    @Query("""
        SELECT
                a.userId as userId,
                a.gender as gender,
                a.lastLogin as lastLogin,
                a.userType as userType,
                a.userStage as userStage,
                a.userPrincipal as userPrincipal
        FROM Admin a
        WHERE a.userId = :userId
        """)
    UserProjection findByUserIdReturningProjection(String userId);@Query("""
        SELECT
                a.userId as userId,
                a.gender as gender,
                a.lastLogin as lastLogin,
                a.userType as userType,
                a.userStage as userStage,
                a.userPrincipal as userPrincipal
        FROM Admin a
        WHERE a.userPrincipal.email = :email
        """)
    UserProjection findByEmailReturningProjection(String email);
}
