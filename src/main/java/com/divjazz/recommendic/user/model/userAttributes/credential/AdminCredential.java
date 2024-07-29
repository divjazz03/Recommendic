package com.divjazz.recommendic.user.model.userAttributes.credential;

import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.User;
import com.fasterxml.jackson.annotation.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.UUID;

@JsonInclude(NON_DEFAULT)
@Entity
public class AdminCredential extends UserCredential {
    @OneToOne(targetEntity = Admin.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    @NotNull
    private User admin;
    @Column(name = "expired")
    private boolean expired;

    protected AdminCredential(){}

    public AdminCredential(Admin admin, String password) {
        super(password);
        this.admin = admin;
        expired = getCreatedAt()
                .isAfter(getCreatedAt().plusYears(1));
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
}
