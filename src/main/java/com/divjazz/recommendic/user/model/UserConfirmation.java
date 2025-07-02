package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.global.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "users_confirmation")
@Getter
@Setter
@NoArgsConstructor
public class UserConfirmation extends Auditable {
    @Column(name = "key")
    private String key;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "expiry", nullable = false, updatable = false)
    private LocalDateTime expiry;


    public UserConfirmation(User user) {
        super();
        this.key = UUID.randomUUID().toString();
        this.userId = user.getUserId();
        this.expiry = LocalDateTime.now().plusHours(24);
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
    @Override
    public int hashCode() {
        var id = getId();
        int result = key != null ? key.hashCode() : 0;
        return (int) (31 * result + id ^ (id >>> 31));
    }
}
