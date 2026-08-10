package com.divjazz.recommendic.security;

import com.divjazz.recommendic.security.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class SseAuthenticationFilter extends OncePerRequestFilter {
    private final AntPathMatcher antPathMatcher;
    private final JWTService jwtService;
    public SseAuthenticationFilter (JWTService jwtService) {
        this.antPathMatcher = new AntPathMatcher();
        this.jwtService = jwtService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getParameter("token");

        if (Objects.isNull(accessToken)) {
            log.warn("No accessToken");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token is required in request parameter 'token'");
            return;
        }
        log.info("Access token {}", accessToken.substring(0,10));
        Jwt jwt = jwtService.decodeToken(accessToken);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, null));
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !antPathMatcher.match("/api/v1/sse/**", request.getRequestURI());
    }
}
