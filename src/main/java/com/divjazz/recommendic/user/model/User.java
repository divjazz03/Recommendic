package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.userAttributes.*;
import static com.divjazz.recommendic.security.constant.Constants.PERMISSION_DELIMITER;


import com.divjazz.recommendic.user.model.userAttributes.confirmation.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;



@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public class User extends Auditable implements UserDetails {

    @Column(nullable = false)
    @Embedded
    private UserName userName;

    @Column(nullable = false)
    private String email;
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    @Column(nullable = false)
    private Address address;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "login_attempts")
    private int loginAttempts;

    @Embedded
    private ProfilePicture profilePicture;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(fetch = FetchType.EAGER,
            mappedBy = "user",
            cascade = CascadeType.REMOVE)
    private UserCredential userCredential;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;



    @OneToOne(mappedBy = "user",
            cascade = CascadeType.REMOVE)
    private UserConfirmation userConfirmation;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean enabled;



    protected User(){}

    public User(UserName userName,
                String email,
                String phoneNumber,
                Gender gender,
                Address address,
                Role role,
                UserCredential userCredential){
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.address = address;
        this.role = role;
        this.userCredential = userCredential;
        this.userId = UUID.randomUUID().toString();
        this.accountNonLocked = true;
        this.accountNonExpired = true;
        this.enabled = false;
    }


    public UserName getUserNameObject() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
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

    public Role getRole() {
        return role;
    }



    public UserName getUserName() {
        return userName;
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {

         return Set.of(new SimpleGrantedAuthority(role.getPermissions()));
    }

    @Override
    public String getPassword() {
        return null;
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
        return userCredential.isExpired();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserCredential getUserCredential() {
        return userCredential;
    }

    public void setUserCredential(UserCredential userCredential) {
        this.userCredential = userCredential;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public UserConfirmation getUserConfirmation() {
        return userConfirmation;
    }

    public void setUserConfirmation(UserConfirmation userConfirmation) {
        this.userConfirmation = userConfirmation;
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
