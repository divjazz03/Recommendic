package com.divjazz.recommendic.user.model.userAttributes.credential;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class UserCredential {
    @Column(name = "password")
    private String password;

    @Column(name = "credential_expired")
    private boolean expired;
    @Column(name = "credential_last_modified")
    private LocalDateTime last_modified;

    protected UserCredential() {
    }

    public UserCredential(String password) {
        this.password = password;
        this.last_modified = LocalDateTime.now();
        expired = LocalDateTime.now().isAfter(last_modified.plusMonths(9));
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getLast_modified() {
        return last_modified;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isExpired() {
        return expired;
    }
}
