package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.confirmation.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype",discriminatorType = DiscriminatorType.STRING)
@Table(name = "users")
public abstract class User extends Auditable implements UserDetails, Serializable {

    @Type(JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "username", nullable = false, columnDefinition = "jsonb")
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

    @Type(JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "address", nullable = false, columnDefinition = "jsonb")
    private Address address;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Type(JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "profile_picture", nullable = false, columnDefinition = "jsonb")
    private ProfilePicture profilePicture;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Type(JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "user_credential", nullable = false, columnDefinition = "jsonb")
    private UserCredential userCredential;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name = "user_stage", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStage userStage;

    @OneToOne(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    private UserConfirmation userConfirmation;

    private boolean accountNonExpired;

    private boolean accountNonLocked;
    private boolean enabled;

    protected User() {
    }

    public User(UserName userName,
                String email,
                String phoneNumber,
                Gender gender,
                Address address,
                Role role,
                UserCredential userCredential) {
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
        this.enabled = true;
    }


    public UserName getUserNameObject() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserName getUserName() {
        return userName;
    }

    public void setUserName(UserName userName) {
        this.userName = userName;
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
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
        return !userCredential.isExpired();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }


    public UserCredential getUserCredential() {
        return userCredential;
    }

    public void setUserCredential(UserCredential userCredential) {
        this.userCredential = userCredential;
    }

    public UserConfirmation getUserConfirmation() {
        return userConfirmation;
    }

    public UserStage getUserStage() {
        return userStage;
    }

    public void setUserConfirmation(UserConfirmation userConfirmation) {
        this.userConfirmation = userConfirmation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(email, user.email)
                && Objects.equals(userId, user.userId)
                && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        var id = getId();
        var res = email != null ? email.hashCode() : 0;
        res = userId != null ? userId.hashCode() : 0;
        return (int) (31 * res + id ^ (id >>> 31));
    }

    public void setUserStage(UserStage userStage) {
        this.userStage = userStage;
    }
}
