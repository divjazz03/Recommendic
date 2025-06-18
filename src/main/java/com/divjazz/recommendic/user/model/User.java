package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
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


@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User extends Auditable implements UserDetails {

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

    private boolean accountNonExpired;

    private boolean accountNonLocked;
    private boolean enabled;

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
        this.enabled = false;
    }

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {

        return Set.of(new SimpleGrantedAuthority(role.getPermissions()));
    }

    @Override
    public String getPassword() {
        return getUserCredential().getPassword();
    }

    @Override
    public String getUsername() {
        return email;
    }

    public UserName getUserNameObject() {return userName;}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(email, user.email)
                && Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUserId(), this.getEmail());
    }
}


