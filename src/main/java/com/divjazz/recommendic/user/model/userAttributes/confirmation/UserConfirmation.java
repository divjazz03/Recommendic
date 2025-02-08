package com.divjazz.recommendic.user.model.userAttributes.confirmation;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Entity
@Table(name = "users_confirmation")
public class UserConfirmation extends Auditable {
    @Column(name = "key")
    private String key;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "expiry", nullable = false, updatable = false)
    private LocalDateTime expiry;


    protected UserConfirmation() {
    }

    public UserConfirmation(User user) {
        super();
        this.key = UUID.randomUUID().toString();
        this.user = user;
        this.expiry = LocalDateTime.now().plusHours(24);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean isExpired() {
        return LocalDateTime.now().isAfter(expiry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserConfirmation that = (UserConfirmation) o;

        return Objects.equals(key, that.key) && Objects.equals(getId(), that.getId());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        var id = getId();
        int result = key != null ? key.hashCode() : 0;
        return (int) (31 * result + id ^ (id >>> 31));
    }
}
