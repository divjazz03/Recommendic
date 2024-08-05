package com.divjazz.recommendic.security;

import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.model.User;
import org.springframework.security.access.method.P;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class ApiAuthentication extends AbstractAuthenticationToken {
    private static final String PASSWORD_PROTECTED = "[PASSWORD_PROTECTED]";
    private static final String EMAIL_PROTECTED = "[EMAIL_PROTECTED]";
    private UserDetails user;
    private String email;
    private String password;


    private ApiAuthentication(User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);

        this.user = user;
        this.email = EMAIL_PROTECTED;
        this.password = PASSWORD_PROTECTED;

    }

    private ApiAuthentication(String email, String password) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.email = email;
        this.password = password;
    }

    public static ApiAuthentication unAuthenticated(String email, String password) {
        return new ApiAuthentication(email, password);
    }
    public static ApiAuthentication authenticated(User user, Collection<? extends GrantedAuthority> authorities) {
        return new ApiAuthentication(user, authorities);
    }
    @Override
    public Object getCredentials() {
        return PASSWORD_PROTECTED;
    }
    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new RuntimeException("You cannot explicitly set authentication");
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    public String getPassword() {
        return this.password;
    }
    public String getEmail() {
        return this.email;
    }
}
