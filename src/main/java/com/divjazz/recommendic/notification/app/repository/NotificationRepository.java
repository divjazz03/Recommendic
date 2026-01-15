package com.divjazz.recommendic.notification.app.repository;

import com.divjazz.recommendic.notification.app.model.AppNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface NotificationRepository extends JpaRepository<AppNotification, Long> {

    @Query("""
            SELECT n
                FROM AppNotification n
                WHERE (n.notificationId = :userId AND
                    (:createdAt IS NULL OR (n.createdAt < :createdAt OR (n.createdAt = :createdAt AND n.id < :id))))
                ORDER BY n.createdAt DESC, n.id DESC
            """)
    List<AppNotification> findNextPage(
            @Param("userId") String userId,
            @Param("createdAt") Instant createdAt,
            @Param("id") Long id,
            Pageable pageable);

    @Transactional(readOnly = true)
    Set<AppNotification> findTop5ByForUserIdOrderByNotificationIdDesc(String userId);

    @Modifying
    @Query("""
            UPDATE AppNotification a
            SET a.seen = true
            WHERE a.notificationId = :notificationId
            """)
    void setNotificationToSeenById(String notificationId);
    @Modifying
    @Query("""
    UPDATE AppNotification a
        SET a.seen = true
        where a.forUserId = :userId
    """)
    void setAllNotificationToSeenForUserId(String userId);
}
