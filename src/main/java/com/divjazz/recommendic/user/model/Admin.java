package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.model.userAttributes.*;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;

import java.util.Collections;
import java.util.Set;

@Entity
public class Admin extends User{

    @Cascade(CascadeType.ALL)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "password")
    private Set<AdminPassword> password;

    protected Admin() {
    }

    public Admin(UserId id, UserName userName, Email email, PhoneNumber phoneNumber, Gender gender,Address address, AdminPassword password) {
        super(id, userName, email, phoneNumber, gender,address);
        this.password = Collections.singleton(password);
    }

    public Set<AdminPassword> getPassword() {
        return password;
    }

    public void setPassword(AdminPassword password) {
        this.password = Collections.singleton(password);
    }
}
