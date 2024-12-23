package com.divjazz.recommendic.security.jwt.service;

import com.divjazz.recommendic.security.TokenType;
import com.divjazz.recommendic.security.domain.Token;
import com.divjazz.recommendic.security.domain.TokenData;
import com.divjazz.recommendic.user.model.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.function.Function;

public interface JwtService {
    String createToken(User user, Function<Token, String> token);
    Optional<String> extractToken(HttpServletRequest httpServletRequest, String tokeType);
    Optional<String> extractToken(HttpServletRequest httpServletRequest);
    void addCookie(HttpServletResponse response, User user, TokenType type);
    void addHeader(HttpServletResponse response, User user, TokenType type);
    <T> T getTokenData(String token, Function<TokenData, T> tokenFunction);
    <T> T getClaimsValue(String token, Function<Claims, T> claimsTFunction);

    void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName);
    boolean validateToken(HttpServletRequest request);
    boolean validateTokenInHeader(HttpServletRequest request);
}
