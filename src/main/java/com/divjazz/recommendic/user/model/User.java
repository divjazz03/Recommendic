package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.userAttributes.*;


import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "tt_user")
public class User extends AbstractEntity<UserId> implements UserDetails {

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
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;
    @OneToOne
    @JoinColumn(name = "profile_picture_id")
    private ProfilePicture profilePicture;

    private String password;



    protected User(){}

    public User(UserId id, UserName userName,String email, String phoneNumber, Gender gender, Address address, UserType userType, String password){
        super(id);
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.address = address;
        this.userType = userType;
        this.password = password;
    }

    public User(UserId id, UserName userName, String email, String phoneNumber, Gender gender, Address address, UserType userType, ProfilePicture profilePicture, String password) {
        super(id);
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.address = address;
        this.userType = userType;
        this.profilePicture = profilePicture;
        this.password = password;
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

    public UserType getUserType() {
        return userType;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = switch (userType){
            case CONSULTANT -> new SimpleGrantedAuthority("CONSULTANT");
            case PATIENT -> new SimpleGrantedAuthority("PATIENT");
            case ADMIN -> new SimpleGrantedAuthority("ADMIN");
        };
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString(){
        return "User: name -> " +
                this.userName.getFullName() +
                ", gender -> " + this.gender.toString().toLowerCase() +
                ", phone number -> " + this.phoneNumber +
                ", email -> " + this.email;
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }
}
