package com.divjazz.recommendic.user.model.userAttributes;

import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.User;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.UUID;

@Entity
public class AdminPassword extends AbstractEntity<UserId>{

    @JoinColumn(name = "admin_password")
    @ManyToOne(cascade = CascadeType.ALL)
    private User assignedAdmin;

    private String password;

    private LocalDate expiryDate;

    protected AdminPassword() {
    }

    public AdminPassword(UserId passwordId, User assignedAdmin, String password) {
        super(passwordId);
        this.assignedAdmin = assignedAdmin;
        this.password = password;
        expiryDate = LocalDate.now().plusYears(1);
    }

    public User getAssignedAdmin() {
        return assignedAdmin;
    }

    public void setAssignedAdmin(User assignedAdmin) {
        this.assignedAdmin = assignedAdmin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getExpiryDate(){
        return expiryDate;
    }
}
