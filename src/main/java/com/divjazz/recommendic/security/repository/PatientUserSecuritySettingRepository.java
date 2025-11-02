package com.divjazz.recommendic.security.repository;

import com.divjazz.recommendic.security.dto.UserSecuritySettingDTO;
import com.divjazz.recommendic.security.model.PatientUserSecuritySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientUserSecuritySettingRepository extends JpaRepository<PatientUserSecuritySetting, Long> {

    Optional<PatientUserSecuritySetting> findByPatient_UserId(String patientId);
    @Query("""
    SELECT NEW com.divjazz.recommendic.security.dto.UserSecuritySettingDTO (
        puss.multiFactorAuthEnabled,
        puss.sessionTimeoutMin,
        puss.loginAlertsEnabled
        ) FROM PatientUserSecuritySetting puss
          WHERE puss.patient.userId = :patientId
    """)
    Optional<UserSecuritySettingDTO> findSecuritySettingByUserId(String patientId);
    @Query("""
    SELECT puss.sessionTimeoutMin
    FROM PatientUserSecuritySetting puss
    WHERE puss.patient.userId = :userId
""")
    Integer findByPatient_UserIdReturningSessionDurationINMinutes(String userId);
}
