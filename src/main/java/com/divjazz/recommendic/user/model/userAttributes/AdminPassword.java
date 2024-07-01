package com.divjazz.recommendic.user.model.userAttributes;

import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.User;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.annotations.Cascade;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.UUID;

@Entity
public class AdminPassword{

    @Id
    private UUID id;
    @JoinColumn(name = "admin_password")
    @ManyToOne(cascade = CascadeType.ALL)
    private Admin assignedAdmin;

    private String password;

    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
    protected AdminPassword() {
    }

    public AdminPassword(UUID passwordId, Admin assignedAdmin, String password) {
        id = passwordId;
        this.assignedAdmin = assignedAdmin;
        this.password = password;
        createdAt = LocalDateTime.now();
        expiryDate = createdAt.plusMonths(6);
    }

    public UUID getId() {
        return id;
    }

    public Admin getAssignedAdmin() {
        return assignedAdmin;
    }

    public void setAssignedAdmin(Admin assignedAdmin) {
        this.assignedAdmin = assignedAdmin;
    }

    public String getPassword() {
        return password;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

}
