package com.divjazz.recommendic.user.model.userAttributes;


import com.google.common.base.MoreObjects;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Email {

    private String email;

    public Email(String email) {
        this.email = email;
    }
    protected Email(){}

    public String asString(){
        return this.email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Email email1 = (Email) o;

        return Objects.equals(email, email1.email);
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }

    @Override
    public String toString(){
        return MoreObjects
                .toStringHelper(this)
                .add("email", email)
                .toString();
    }
}
