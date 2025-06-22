package com.divjazz.recommendic.notification.model;

import com.divjazz.recommendic.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
    private String header;
    private String summary;
    private String userId;
    private boolean seen;
}
