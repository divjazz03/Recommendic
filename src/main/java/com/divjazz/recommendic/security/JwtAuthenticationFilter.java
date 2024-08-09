package com.divjazz.recommendic.security;

import com.divjazz.recommendic.security.domain.TokenData;
import com.divjazz.recommendic.security.exception.InvalidTokenException;
import com.divjazz.recommendic.security.exception.TokenNotFoundException;
import com.divjazz.recommendic.security.jwt.service.JwtService;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.service.GeneralUserService;
import static com.divjazz.recommendic.security.TokenType.*;
import static com.divjazz.recommendic.utils.RequestUtils.getErrorResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;


import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final CustomUserDetailsService userService;
    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            if (!request.getRequestURI().equals("/user/login") && !request.getRequestURI().matches("/api/v1/(patient|consultant|admin)/create")) {
                final Optional<String> authorizationAccessToken = jwtService.extractToken(request, ACCESS.getValue());
                final String authorizationHeader = request.getHeader("Authorization");
                String username = null;
                String jwtToken = null;
                if (authorizationAccessToken.isPresent()) {

                    jwtToken = authorizationAccessToken.get();
                    username = jwtService.getClaimsValue(jwtToken, Claims::getSubject);

                } else if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    jwtToken = authorizationHeader.substring(7);
                    username = jwtService.getClaimsValue(jwtToken, Claims::getSubject);
                } else throw new TokenNotFoundException();

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userService.loadUserByUsername(username);

                    if (jwtService.validateToken(request)) {
                        var userPermissions = jwtService.getTokenData(jwtToken, TokenData::getGrantedAuthorities);
                        var authentication = ApiAuthentication.authenticated(userDetails, userPermissions);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        filterChain.doFilter(request,response);
                    } else {
                        throw new InvalidTokenException();
                    }
                } else {
                    throw new AuthenticationException("No user found in authentication context");
                }
            } else {
                filterChain.doFilter(request,response);
            }
        } catch (UserNotFoundException | InvalidTokenException | TokenNotFoundException e) {
            var errorResponse = getErrorResponse(
                    request,
                    response,
                    e,
                    HttpStatus.EXPECTATION_FAILED
            );
            try (var out = response.getOutputStream()){
                var mapper = new ObjectMapper();
                mapper.writeValue(out, errorResponse);
            }
        }
    }
}
