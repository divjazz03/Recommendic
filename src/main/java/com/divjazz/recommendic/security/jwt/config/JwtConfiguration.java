package com.divjazz.recommendic.security.jwt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class JwtConfiguration {
    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.secret}")
    private String secret;

    public Long getExpiration() {
        return expiration;
    }

    public String getSecret() {
        return secret;
    }
}
