package com.divjazz.recommendic.user.model.userAttributes;

import com.divjazz.recommendic.user.model.Admin;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
public class AdminPassword extends AbstractEntity<UserId> {

    @JoinColumn(name = "name_admin")
    @ManyToOne
    private Admin assignedAdmin;
    @Column(name = "password", nullable = false)
    private String password;

    protected AdminPassword() {
    }

    public AdminPassword(Admin assignedAdmin, String password) {
        this.assignedAdmin = assignedAdmin;
        this.password = password;
    }
}
