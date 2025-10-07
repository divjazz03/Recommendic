package com.divjazz.recommendic.notification.app.repository;

import com.divjazz.recommendic.notification.app.model.AppNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<AppNotification, Long> {
    Page<AppNotification> findAllByForUserId(String userId, Pageable pageable);
    Page<AppNotification> findAllByForUserIdAndSeenIsFalse(String userId, Pageable pageable);
}
