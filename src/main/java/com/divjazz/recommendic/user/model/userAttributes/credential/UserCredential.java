package com.divjazz.recommendic.user.model.userAttributes.credential;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users_credential")
public class UserCredential extends Auditable {
    private String password;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean expired;

    protected UserCredential() {
    }

    public UserCredential(String password) {
        super();
        this.password = password;
        expired = LocalDateTime.now().isAfter(getCreatedAt().plusMonths(9));
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isExpired() {
        return expired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserCredential that = (UserCredential) o;

        return Objects.equals(password, that.password) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        var id = getId();
        var res = password != null ? password.hashCode() : 0;
        return (int) (31 * res + id ^ (id >>> 31));
    }
}
