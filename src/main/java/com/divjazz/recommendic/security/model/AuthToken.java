package com.divjazz.recommendic.security.model;

import com.divjazz.recommendic.global.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthToken extends Auditable {

    private String token;
    private String userId;
    private Instant expiresAt;
    private String userType;

}
