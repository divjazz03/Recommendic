package com.divjazz.recommendic.security.jwt.service.impl;

import com.divjazz.recommendic.security.TokenType;
import com.divjazz.recommendic.security.domain.Token;
import com.divjazz.recommendic.security.domain.TokenData;
import com.divjazz.recommendic.security.jwt.config.JwtConfiguration;
import com.divjazz.recommendic.security.jwt.service.JwtService;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.GeneralUserService;

import static com.divjazz.recommendic.security.constant.Constants.*;
import static com.divjazz.recommendic.security.TokenType.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import static java.util.Optional.empty;
import static org.springframework.boot.web.server.Cookie.SameSite.NONE;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


@Service
public class JwtServiceImpl implements JwtService {
    private final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);

    private final GeneralUserService userService;

    private JwtConfiguration jwtConfiguration;

    public JwtServiceImpl(GeneralUserService userService, JwtConfiguration jwtConfiguration) {
        this.userService = userService;
        this.jwtConfiguration = jwtConfiguration;
    }

    private final Supplier<SecretKey> keySupplier = () -> Keys
            .hmacShaKeyFor(
                    Decoders.BASE64.decode(jwtConfiguration.getSecret())
            );

    private final Function<String, Claims> claimsFunction = token ->
            Jwts.parser()
                    .verifyWith(keySupplier.get())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

    private final Function<String, String> subject = token -> getClaimsValue(token, Claims::getSubject);

    private final BiFunction<HttpServletRequest, String, Optional<String>> extractToken = (request, cookieName) ->
            Optional.of(Arrays.stream(
                            request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} :
                                    request.getCookies())
                    .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                    .map(Cookie::getValue)
                    .findAny()).orElse(empty());
    private final Function<HttpServletRequest, Optional<String>> extractTokenFromHeader = httpServletRequest -> {
        var jwtToken = httpServletRequest.getHeader("Authorization");
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
            return Optional.of(jwtToken);
        }
        return Optional.empty();
    };

    private final BiFunction<HttpServletRequest, String, Optional<Cookie>> extractCookie = (request, cookieName) ->
            Optional.of(Arrays.stream(
                            request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} :
                                    request.getCookies())
                    .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                    .findAny()).orElse(empty());

    private final Supplier<JwtBuilder> builder = () ->
            Jwts.builder()
                    .header().add(Map.of(TYPE, JWT_TYPE))
                    .and()
                    .audience().add("RECOMMENDIC")
                    .and()
                    .id(String.valueOf(UUID.randomUUID()))
                    .issuedAt(Date.from(Instant.now()))
                    .notBefore(new Date())
                    .signWith(keySupplier.get(), Jwts.SIG.HS512);

    private final BiFunction<User, TokenType, String> buildToken = (user, type) ->
            Objects.equals(type, ACCESS) ? builder.get()
                    .subject(user.getEmail())
                    .claim(PERMISSIONS, user.getRole().getPermissions())
                    .claim(ROLE, user.getRole().getName())
                    .expiration(Date.from(Instant.now().plusSeconds(jwtConfiguration.getExpiration())))
                    .compact() : builder.get()
                    .subject(user.getEmail())
                    .compact();

    private final TriConsumer<HttpServletResponse, User, TokenType> addCookie = (response, user, type) -> {
        switch (type) {
            case ACCESS -> {
                var accessToken = createToken(user, Token::getAccess);
                var cookie = new Cookie(type.getValue(), accessToken);
                cookie.setHttpOnly(true);
                // cookie.setSecure(true);
                cookie.setMaxAge(2 * 60 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
            case REFRESH -> {
                var refreshToken = createToken(user, Token::getRefresh);
                var cookie = new Cookie(type.getValue(), refreshToken);
                cookie.setHttpOnly(true);
                // cookie.setSecure(true);
                cookie.setMaxAge(60 * 60 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
        }
    };

    private final BiConsumer<HttpServletResponse, User> addHeader = (response, user) -> {
        var accessToken = createToken(user,Token::getAccess);
        response.addHeader("Authorization", String.format("Bearer %s", accessToken));
    };

    public <T> T getClaimsValue(String token, Function<Claims, T> claims) {
        return claimsFunction.andThen(claims).apply(token);
    }

    @Override
    public String createToken(User user, Function<Token, String> tokenFunction) {
        var token = Token.builder()
                .access(buildToken.apply(user, ACCESS))
                .refresh(buildToken.apply(user, REFRESH))
                .build();
        return tokenFunction.apply(token);
    }

    @Override
    public Optional<String> extractToken(HttpServletRequest httpServletRequest, String cookieName) {
        return extractToken.apply(httpServletRequest, cookieName);
    }

    @Override
    public Optional<String> extractToken(HttpServletRequest httpServletRequest) {
        return extractTokenFromHeader.apply(httpServletRequest);
    }

    @Override
    public void addCookie(HttpServletResponse response, User user, TokenType type) {
        addCookie.accept(response, user, type);
    }

    @Override
    public void addHeader(HttpServletResponse response, User user, TokenType type) {
        addHeader.accept(response,user);
    }

    @Override
    public <T> T getTokenData(String token, Function<TokenData, T> tokenFunction) {
        return tokenFunction.apply(
                TokenData.builder()
                        .valid(
                                Objects.equals(userService.retrieveUserByEmail(subject.apply(token)).getEmail(), claimsFunction.apply(token).getSubject())
                        )
                        .expired(Instant.now().isAfter(getClaimsValue(token, Claims::getExpiration).toInstant()))
                        .authorities(List.of(new SimpleGrantedAuthority((String) claimsFunction.apply(token).get("permissions"))))
                        .claims(claimsFunction.apply(token))
                        .user(userService.retrieveUserByEmail(subject.apply(token)))
                        .build()
        );
    }

    @Override
    public void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        var optionalCookie = extractCookie.apply(request, cookieName);
        if (optionalCookie.isPresent()) {
            var cookie = optionalCookie.get();
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    @Override
    public boolean validateToken(HttpServletRequest request) {
        var token = extractToken(request , ACCESS.getValue());
        if (token.isPresent()) {
            return getTokenData(token.get(), TokenData::isValid) ;
        }
        return false;
    }

    @Override
    public boolean validateTokenInHeader(HttpServletRequest request) {
        var token = extractToken(request);
        if (token.isPresent()) {
            return getTokenData(token.get(), TokenData::isValid);
        }
        return false;
    }
}
