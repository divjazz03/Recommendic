package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.model.userAttributes.*;


import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class User extends AbstractEntity<UserId> {

    private UserId id;
    private UserName userName;
    private Email email;

    private PhoneNumber phoneNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

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
