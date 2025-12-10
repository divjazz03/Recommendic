package com.divjazz.recommendic.notification.app.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.notification.app.enums.NotificationCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "app_notification")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AppNotification extends Auditable {
    @Column(name = "notification_id", updatable = false, insertable = false)
    private String notification_id;
    @Column(name = "header")
    private String header;
    @Column(name = "summary")
    private String summary;
    @Column(name = "user_id")
    private String forUserId;
    @Column(name = "seen")
    private boolean seen;
    @JdbcTypeCode(value = SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private NotificationCategory category;
    @Column(name = "subject_id")
    private String subjectId;
}
