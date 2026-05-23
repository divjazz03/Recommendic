package com.divjazz.recommendic.global;


import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.of("Anonymous");
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return Optional.of(jwt.getSubject());
        }
        String principal = (String) authentication.getPrincipal();

        return Optional.ofNullable(principal);
    }
}
