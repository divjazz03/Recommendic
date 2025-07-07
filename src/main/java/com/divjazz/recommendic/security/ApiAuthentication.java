package com.divjazz.recommendic.security;

import com.divjazz.recommendic.user.model.User;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class ApiAuthentication extends UsernamePasswordAuthenticationToken {
    private static final String PASSWORD_PROTECTED = "[PASSWORD_PROTECTED]";
    private static final String EMAIL_PROTECTED = "[EMAIL_PROTECTED]";
    private User user;
    @Getter
    private final String email;
    @Getter
    private final String password;


    private ApiAuthentication(UserDetails user, Object credentials, Collection<? extends  GrantedAuthority> authorities) {
        super(user, PASSWORD_PROTECTED,authorities);
        this.user = (User) user;
        this.email = EMAIL_PROTECTED;
        this.password = PASSWORD_PROTECTED;
    }

    private ApiAuthentication(String email, String password) {
        super(email,password);
        this.email = email;
        this.password = password;
    }


    public static ApiAuthentication unAuthenticated(String email, String password) {
        return new ApiAuthentication(email, password);
    }
    public static ApiAuthentication authenticated(Object principal, Object credentials,
                                                  Collection<? extends GrantedAuthority> authorities) {
        return new ApiAuthentication((User) principal, credentials, authorities);
    }
    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getPrincipal() {
        return isAuthenticated() ? user : email;
    }

}
