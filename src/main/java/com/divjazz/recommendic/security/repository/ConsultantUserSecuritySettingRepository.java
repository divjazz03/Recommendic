package com.divjazz.recommendic.security.repository;

import com.divjazz.recommendic.security.dto.UserSecuritySettingDTO;
import com.divjazz.recommendic.security.model.ConsultantUserSecuritySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultantUserSecuritySettingRepository extends JpaRepository<ConsultantUserSecuritySetting, Long> {
    Optional<ConsultantUserSecuritySetting> findByConsultant_UserId(String consultantId);

    @Query("""
    SELECT new com.divjazz.recommendic.security.dto.UserSecuritySettingDTO (
        cuss.multiFactorAuthEnabled,
        cuss.sessionTimeoutMin,
        cuss.loginAlertsEnabled
        ) FROM ConsultantUserSecuritySetting cuss
          WHERE cuss.consultant.userId = :userId
    """)
    Optional<UserSecuritySettingDTO> findSettingByUserId(String userId);

    @Query("""
    SELECT cuss.sessionTimeoutMin
    FROM ConsultantUserSecuritySetting cuss
    WHERE cuss.consultant.userId = :userId
""")
    Integer findByConsultant_UserIdReturningSessionDurationINMinutes(String userId);
}
