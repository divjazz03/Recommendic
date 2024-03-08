package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.model.userAttributes.*;


import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@MappedSuperclass
public abstract class User extends AbstractEntity<UserId>{

    @Column(nullable = false)
    @Embedded
    private UserName userName;
    @Column(name = "email", nullable = false)
    private Email email;

    @Column(name = "email", nullable = false)
    private PhoneNumber phoneNumber;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Address address;

    protected User(){}

    public User(UserId id, UserName userName, Email email, PhoneNumber phoneNumber, Gender gender){
        super(id);
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
    }

    public UserName getUserName() {
        return userName;
    }

    public Email getEmail() {
        return email;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setUserName(UserName userName) {
        this.userName = userName;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Gender getGender() {
        return gender;
    }
}
