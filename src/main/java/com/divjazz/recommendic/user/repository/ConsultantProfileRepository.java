package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultantProfileRepository extends JpaRepository<ConsultantProfile, Long> {
    @Query("""
        SELECT c.profile from Consultant c
        WHERE c.userId = :id
    """)
    Optional<ConsultantProfile> findByConsultantId(String id);

    @Query("""
    select c.userName
        from ConsultantProfile c
        where c.consultant.userId = :userId
    """)
    Optional<UserName> findUserNameByConsultant_UserId(String userId);
}
