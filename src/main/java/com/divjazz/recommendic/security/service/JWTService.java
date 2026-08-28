package com.divjazz.recommendic.security.service;

import com.divjazz.recommendic.security.dto.AccessTokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JWTService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    public AccessTokenDTO generateToken (UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(1, ChronoUnit.HOURS);
        List<String> roles = userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("recommendic")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .build();
        String tokenValue =  jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new AccessTokenDTO(tokenValue, expiresAt.toString());
    }

    public Jwt decodeToken (String accessToken) {
       return jwtDecoder.decode(accessToken);
    }
}
