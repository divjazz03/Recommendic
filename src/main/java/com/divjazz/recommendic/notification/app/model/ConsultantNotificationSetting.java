package com.divjazz.recommendic.notification.app.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.user.model.Consultant;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "consultant_notification_setting")
@Getter
@AllArgsConstructor
@Setter
public class ConsultantNotificationSetting extends Auditable {
    private boolean emailNotificationEnabled;
    private boolean smsNotificationEnabled;
    private boolean appointmentRemindersEnabled;
    private boolean labResultsUpdateEnabled;
    private boolean systemUpdatesEnabled;
    private boolean marketingEmailEnabled;
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @OneToOne
    private Consultant consultant;

    public ConsultantNotificationSetting() {
        this.emailNotificationEnabled = true;
        this.smsNotificationEnabled = true;
        this.appointmentRemindersEnabled = true;
        this.labResultsUpdateEnabled = true;
        this.systemUpdatesEnabled = false;
        this.marketingEmailEnabled = false;
        this.consultant = null;
    }
}
