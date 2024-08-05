package com.divjazz.recommendic.user.model.userAttributes.confirmation;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;


@Entity
@Table(name = "users_confirmation")
public class UserConfirmation extends Auditable {
    @Column(name = "key")
    private String key;

    @Column(name = "expired")
    private Boolean expired;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;


    protected UserConfirmation(){
        super();
        this.key = UUID.randomUUID().toString();
    }

    public String getKey() {
        return key;
    }

    public Boolean getExpired() {
        return expired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserConfirmation that = (UserConfirmation) o;

        return Objects.equals(key, that.key) && Objects.equals(getId(), that.getId());
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
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
