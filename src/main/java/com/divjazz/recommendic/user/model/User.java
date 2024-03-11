package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.model.userAttributes.*;


import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;

@MappedSuperclass
public abstract class User extends AbstractEntity<UserId>{

    @Column(nullable = false)
    @Embedded
    private UserName userName;
    @Embedded
    @Column(name = "email", nullable = false)
    private Email email;

    @Embedded
    @Column(name = "email", nullable = false)
    private PhoneNumber phoneNumber;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    @Column(nullable = false)
    private Address address;



    protected User(){}

    public User(UserId id, UserName userName, Email email, PhoneNumber phoneNumber, Gender gender, Address address){
        super(id);
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.address = address;

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
