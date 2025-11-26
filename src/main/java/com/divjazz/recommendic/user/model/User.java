package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;
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
@Builder
public class User extends Auditable {

    @Column(name = "user_id", updatable = false, insertable = false)
    @Generated(event = EventType.INSERT)
    @Setter
    private String userId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "last_login")
    @Setter
    private LocalDateTime lastLogin;
    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;
    @Column(name = "user_stage", nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private UserStage userStage;
    @Embedded
    private UserPrincipal userPrincipal;

    public User(
                String email,
                Gender gender,
                Role role,
                UserCredential userCredential, UserType userType) {
        this.gender = gender;
        this.userType = userType;
        userPrincipal = new UserPrincipal(email,userCredential,role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return userId != null && Objects.equals(this.userPrincipal.getUsername(), user.getUserPrincipal().getUsername())
                && Objects.equals(userId, user.userId);
    }
}


