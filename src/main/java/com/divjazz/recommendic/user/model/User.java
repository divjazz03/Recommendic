package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class User extends Auditable implements UserDetails {
    @Column(nullable = false)
    private String email;
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "last_login")
    @Setter
    private LocalDateTime lastLogin;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "user_credential", nullable = false, columnDefinition = "jsonb")
    private UserCredential userCredential;
    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;
    @Column(name = "user_stage", nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private UserStage userStage;
    @Setter
    private boolean accountNonExpired;
    @Setter
    private boolean accountNonLocked;
    @Setter
    private boolean enabled;

    public User(
                String email,
                Gender gender,
                Role role,
                UserCredential userCredential, UserType userType) {
        this.email = email;
        this.gender = gender;
        this.role = role;
        this.userCredential = userCredential;
        this.userId = UUID.randomUUID().toString();
        this.accountNonLocked = true;
        this.accountNonExpired = true;
        this.userType = userType;
        this.enabled = false;
    }

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {

        return Set.of(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    public String getPassword() {
        return getUserCredential().getPassword();
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


