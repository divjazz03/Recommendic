package com.divjazz.recommendic.security.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.user.model.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "patient_security_setting")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatientUserSecuritySetting extends Auditable {
    @Column(name = "multi_factor_auth_enabled")
    private boolean multiFactorAuthEnabled;
    @Column(name = "session_timeout_min")
    private int sessionTimeoutMin;
    @Column(name = "login_alerts_enabled")
    private boolean loginAlertsEnabled;
    @OneToOne
    @JoinColumn(name = "patient_id", updatable = false)
    private Patient patient;
}
