package com.divjazz.recommendic.security;

import com.divjazz.recommendic.security.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class LogoutAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    Logger log = LoggerFactory.getLogger(LogoutAuthenticationFilter.class);

    public LogoutAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals("/user/logout")) {
            jwtService.removeCookie(request, response, TokenType.ACCESS.getValue());
            doFilter(request, response, filterChain);
        } else {
            doFilter(request, response, filterChain);
        }
    }
}
