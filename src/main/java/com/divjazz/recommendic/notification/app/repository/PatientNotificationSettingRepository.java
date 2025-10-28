package com.divjazz.recommendic.notification.app.repository;

import com.divjazz.recommendic.notification.app.model.PatientNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientNotificationSettingRepository extends JpaRepository<PatientNotificationSetting, Long> {
    Optional<PatientNotificationSetting> findByPatient_UserId(String userId);
}
