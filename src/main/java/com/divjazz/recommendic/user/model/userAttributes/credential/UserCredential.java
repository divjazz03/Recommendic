package com.divjazz.recommendic.user.model.userAttributes.credential;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;

import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public class UserCredential extends Auditable {
    private String password;

    protected UserCredential(){}

    public UserCredential(String password) {
        super();
        this.password = password;
    }

    public String getPassword() {
        return password;
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
