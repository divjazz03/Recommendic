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
    private User user;
    @CreatedDate
    @NotNull
    private LocalDateTime expiryDate = getCreatedAt().plusMonths(6L);

    protected AdminCredential(){}

    public AdminCredential(Admin admin, String password, UUID referenceId) {
        super(password, referenceId);
        this.user = admin;
    }

    public User getUser() {
        return user;
    }

    public void setUser(Admin user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
