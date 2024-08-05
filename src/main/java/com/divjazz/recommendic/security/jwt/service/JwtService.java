package com.divjazz.recommendic.security.jwt.service;

import com.divjazz.recommendic.security.TokenType;
import com.divjazz.recommendic.security.domain.Token;
import com.divjazz.recommendic.security.domain.TokenData;
import com.divjazz.recommendic.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.function.Function;

public interface JwtService {
    String createToken(User user, Function<Token, String> token);
    Optional<String> extractToken(HttpServletRequest httpServletRequest, String tokeType);
    void addCookie(HttpServletResponse response, User user, TokenType type);
    <T> T getTokenData(String token, Function<TokenData, T> tokenFunction);

    void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName);
}
