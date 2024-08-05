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

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


@Service
public class JwtServiceImpl extends JwtConfiguration implements JwtService {
    private final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);
    private final GeneralUserService userService;


    private final Supplier<SecretKey> keySupplier = () -> Keys
            .hmacShaKeyFor(
                    Decoders.BASE64.decode(getSecret())
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
                    .subject(user.getUserId())
                    .claim(PERMISSIONS, user.getAuthorities())
                    .claim(ROLE, user.getRole())
                    .expiration(Date.from(Instant.now().plusSeconds(getExpiration())))
                    .compact() : builder.get()
                    .subject(user.getUserId())
                    .expiration(Date.from(Instant.now().plusSeconds(getExpiration())))
                    .compact();

    private final TriConsumer<HttpServletResponse, User, TokenType> addCookie = (response, user, type) -> {
        switch (type) {
            case ACCESS -> {
                var accessToken = createToken(user, Token::getAccess);
                var cookie = new Cookie(type.getValue(), accessToken);
                cookie.setHttpOnly(true);
                // cookie.setSecure(true);
                cookie.setMaxAge(2 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
            case REFRESH -> {
                var refreshToken = createToken(user, Token::getRefresh);
                var cookie = new Cookie(type.getValue(), refreshToken);
                cookie.setHttpOnly(true);
                // cookie.setSecure(true);
                cookie.setMaxAge(2 * 60 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
        }
    };

    public <T> T getClaimsValue(String token, Function<Claims, T> claims) {
        return claimsFunction.andThen(claims).apply(token);
    }


    public Function<String, List<? extends GrantedAuthority>> authorities = token ->
            commaSeparatedStringToAuthorityList(new StringJoiner(PERMISSIONS)
                    .add(claimsFunction.apply(token).get(PERMISSIONS, String.class))
                    .add(claimsFunction.apply(token).get(ROLE, String.class)).toString());


    public JwtServiceImpl(GeneralUserService userService) {
        this.userService = userService;
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
    public void addCookie(HttpServletResponse response, User user, TokenType type) {
        addCookie.accept(response, user, type);
    }

    @Override
    public <T> T getTokenData(String token, Function<TokenData, T> tokenFunction) {
        return tokenFunction.apply(
                TokenData.builder()
                        .valid(Objects.equals(userService.retrieveUserByUserId(subject.apply(token)).getUserId(), claimsFunction.apply(token).getSubject()))
                        .authorities(authorities.apply(token))
                        .claims(claimsFunction.apply(token))
                        .user(userService.retrieveUserByUserId(subject.apply(token)))
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
            return getTokenData(token.get(), TokenData::isValid);
        }
        return false;
    }


}
