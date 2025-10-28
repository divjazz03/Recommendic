package com.divjazz.recommendic.notification.app.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.user.model.Patient;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "patient_notification_setting")
@Getter
@AllArgsConstructor
@Setter
public class PatientNotificationSetting extends Auditable {
    private boolean emailNotificationEnabled;
    private boolean smsNotificationEnabled;
    private boolean appointmentRemindersEnabled;
    private boolean labResultsUpdateEnabled;
    private boolean systemUpdatesEnabled;
    private boolean marketingEmailEnabled;
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @OneToOne
    private Patient patient;

    public PatientNotificationSetting() {
        this.emailNotificationEnabled = true;
        this.smsNotificationEnabled = true;
        this.appointmentRemindersEnabled = true;
        this.labResultsUpdateEnabled = true;
        this.systemUpdatesEnabled = false;
        this.marketingEmailEnabled = false;
        this.patient = null;
    }
}
