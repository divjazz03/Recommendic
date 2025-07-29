package com.divjazz.recommendic.security.filter;

import com.divjazz.recommendic.security.SessionUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null
                && session.getAttribute("role") != null
                && session.getAttribute("email") != null
                && session.getAttribute("authorities") != null) {

            String email = (String) session.getAttribute("email");
            String role = (String) session.getAttribute("role");
            if (session.getAttribute("authorities") instanceof String authorities ) {
                try {
                    Collection<? extends GrantedAuthority> grantedAuthorities =  Arrays.stream(authorities.split(":"))
                            .map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
                    var userSession = new SessionUser(email,role,grantedAuthorities);
                    Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(userSession,null,grantedAuthorities);

                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    securityContext.setAuthentication(authentication);
                    SecurityContextHolder.setContext(securityContext);
                } catch (Exception e) {
                    log.error("Error authenticating: {}", e.getMessage());
                }
            }

        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().matches("^/api/v1/auth/login") ||
                request.getRequestURI().matches("^/api-docs") ||
                request.getRequestURI().matches("^/swagger-ui/.*") ||
                request.getRequestURI().matches("^/actuator?(/.*)") ||
                request.getRequestURI().matches("^/favicon.ico") ||
                request.getRequestURI().matches("^/api/v1/medical-categories") ||
                request.getRequestURI().matches("^/api/v1/users");
    }
}
