package com.divjazz.recommendic.global;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        var principal = authentication.getPrincipal();
        if (principal instanceof String s) {
            return Optional.of(s);
        }
        return Optional.of(((UserDetails) authentication.getPrincipal()).getUsername());
    }
}
