package com.divjazz.recommendic.user.repository;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {

    @Query("""
        SELECT pf.userName
            FROM PatientProfile pf
            WHERE pf.patient.userId = :userId
    """)
    Optional<UserName> findUserNameByPatient_UserId(String userId);
}
