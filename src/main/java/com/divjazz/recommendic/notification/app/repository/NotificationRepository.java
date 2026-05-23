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

    @Query(value = """
            SELECT *
            FROM notifications n
            WHERE n.user_id = CAST(:userId AS TEXT)
                        AND (CAST (:createdAt AS TIMESTAMP) IS NULL
                                             OR :id IS NULL
                                             OR(n.created_at, n.notification_id) < (CAST (:createdAt AS TIMESTAMP), CAST(:id AS TEXT)))
            ORDER BY n.created_at DESC , n.notification_id DESC
            LIMIT :limit + 1
            """, nativeQuery = true)
    List<AppNotification> findNextPage(
            @Param("userId") String userId,
            @Param("createdAt") Instant createdAt,
            @Param("id") String id,
            @Param("limit") Integer limit);

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
