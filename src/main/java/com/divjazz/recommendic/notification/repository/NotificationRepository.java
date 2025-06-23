package com.divjazz.recommendic.notification.repository;

import com.divjazz.recommendic.notification.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByForUserId(String userId, Pageable pageable);
    Page<Notification> findAllByForUserIdAndSeenIsFalse(String userId, Pageable pageable);
}
