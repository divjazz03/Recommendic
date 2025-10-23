package com.divjazz.recommendic.security.filter;

import com.divjazz.recommendic.security.SessionUser;
import com.divjazz.recommendic.security.config.WebSecurityConfig;
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
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
public class AuthFilter extends OncePerRequestFilter {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null
                && session.getAttribute("role") != null
                && session.getAttribute("email") != null
                && session.getAttribute("authorities") != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String email = (String) session.getAttribute("email");
            String role = (String) session.getAttribute("role");
            Collection<? extends GrantedAuthority> grantedAuthorities = new HashSet<>();
            Object authoritiesAttr = session.getAttribute("authorities");
            if (authoritiesAttr instanceof String authorities ) {
                try {
                     grantedAuthorities =  Arrays.stream(authorities.split(":"))
                            .map(SimpleGrantedAuthority::new).collect(Collectors.toSet());


                } catch (Exception e) {
                    log.error("Error authenticating: {}", e.getMessage());
                }

            } else if (authoritiesAttr instanceof Collection<?> list) {
                grantedAuthorities = list.stream()
                        .filter(String.class::isInstance)
                        .map(a -> new SimpleGrantedAuthority((String) a))
                        .collect(Collectors.toSet());
            }
            var userSession = new SessionUser(email, role, grantedAuthorities);
            Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(userSession,null,grantedAuthorities);

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextRepository repo = new HttpSessionSecurityContextRepository();
            repo.saveContext(securityContext,request,response);
            SecurityContextHolder.setContext(securityContext);

        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return WebSecurityConfig.getWHITELIST_PATHS().stream()
                .anyMatch(skipPath -> antPathMatcher.match(skipPath, request.getRequestURI()));
    }
}
