package com.divjazz.recommendic.notification.app.repository;

import com.divjazz.recommendic.notification.app.model.ConsultantNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultantNotificationSettingRepository extends JpaRepository<ConsultantNotificationSetting, Long> {
    Optional<ConsultantNotificationSetting> findByConsultant_UserId(String userId);
}
