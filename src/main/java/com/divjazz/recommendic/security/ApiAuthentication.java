package com.divjazz.recommendic.security;

import com.divjazz.recommendic.user.model.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class ApiAuthentication extends UsernamePasswordAuthenticationToken {
    private static final String PASSWORD_PROTECTED = "[PASSWORD_PROTECTED]";
    private static final String EMAIL_PROTECTED = "[EMAIL_PROTECTED]";
    private User user;
    private final String email;
    private final String password;


    private ApiAuthentication(User user) {
        super(user, PASSWORD_PROTECTED,user.getAuthorities());
        this.user = user;
        this.email = EMAIL_PROTECTED;
        this.password = PASSWORD_PROTECTED;
        super.setAuthenticated(true);
    }

    private ApiAuthentication(String email, String password) {
        super(email,password);
        this.email = email;
        this.password = password;
    }

    public static ApiAuthentication unAuthenticated(String email, String password) {
        return new ApiAuthentication(email, password);
    }
    public static ApiAuthentication authenticated(UserDetails user) {
        return new ApiAuthentication((User) user);
    }
    @Override
    public Object getCredentials() {
        return password;
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
