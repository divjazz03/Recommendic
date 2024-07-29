package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.*;


import jakarta.persistence.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;


@MappedSuperclass
public sealed abstract class User extends Auditable implements UserDetails permits Admin, Consultant, Patient {

    @Column(nullable = false)
    @Embedded
    private UserName userName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    @Column(nullable = false)
    private Address address;
    @Embedded
    @Column(nullable = false)
    private ProfilePicture profilePicture;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean enabled;



    protected User(){}

    public User(UserName userName,String email, String phoneNumber, Gender gender, Address address){
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.address = address;
    }


    public UserName getUserNameObject() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setUserName(UserName userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }




    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(email, user.email) && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        var id = getId();
        var res =  email != null ? email.hashCode() : 0;
        return (int) (31 * res + id ^ (id >>> 31));
    }
}
