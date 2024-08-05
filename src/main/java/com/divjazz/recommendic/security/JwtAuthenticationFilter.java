package com.divjazz.recommendic.security;

import com.divjazz.recommendic.security.domain.TokenData;
import com.divjazz.recommendic.security.jwt.service.JwtService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import static com.divjazz.recommendic.security.TokenType.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final GeneralUserService generalUserService;
    public JwtAuthenticationFilter(JwtService jwtService, GeneralUserService generalUserService) {
        this.jwtService = jwtService;
        this.generalUserService = generalUserService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (jwtService.validateToken(request) &&
                SecurityContextHolder.getContext().getAuthentication() == null &&
                jwtService.extractToken(request, ACCESS.getValue()).isPresent()) {
            String token = jwtService.extractToken(request, ACCESS.getValue()).get();
            var user = jwtService.getTokenData(token, TokenData::getUser);
            var authorities = jwtService.getTokenData(token, TokenData::getGrantedAuthorities);

            var myAuthentication = ApiAuthentication.authenticated(user,authorities);
            SecurityContextHolder.getContext().setAuthentication(myAuthentication);


        }
        filterChain.doFilter(request,response);
    }
}
