package com.divjazz.recommendic.notification.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.notification.enums.NotificationCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notification extends Auditable {
    @Column(name = "header")
    private String header;
    @Column(name = "summary")
    private String summary;
    @Column(name = "user_id")
    private String forUserId;
    @Column(name = "seen")
    private boolean seen;
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private NotificationCategory category;
}
