package com.divjazz.recommendic.security;

import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {
    @Setter
    private boolean accountNonExpired;
    @Setter
    private boolean accountNonLocked;
    @Setter
    private boolean enabled;
    @Column(nullable = false)
    private String email;
    @ManyToOne
    @JoinColumn(name = "role")
    @Getter
    private Role role;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "user_credential", nullable = false, columnDefinition = "jsonb")
    @Getter
    private UserCredential userCredential;

    public UserPrincipal(String email, UserCredential userCredential, Role role) {
        this.role = role;
        this.userCredential = userCredential;
        this.email = email;
        this.enabled = true;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
    }

    @Override
    @Transient
    public Set<? extends GrantedAuthority> getAuthorities() {

        return Set.of(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    public String getPassword() {
        return userCredential.getPassword();
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
}
