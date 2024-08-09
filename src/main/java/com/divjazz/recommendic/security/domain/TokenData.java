package com.divjazz.recommendic.security.domain;

import com.divjazz.recommendic.user.model.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class TokenData {
    private UserDetails user;
    private Claims claims;
    private boolean valid;

    private boolean expired;
    private List<? extends GrantedAuthority> grantedAuthorities;

    public UserDetails getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isExpired() {
        return expired;
    }

    public boolean isValid() {
        return valid;
    }


    public List<? extends GrantedAuthority> getGrantedAuthorities() {
        return grantedAuthorities;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TokenData tokenData;

        private Builder() {
            this.tokenData = new TokenData();
        }
        public Builder user(UserDetails user) {
            this.tokenData.user = user;
            return this;
        }
        public Builder claims(Claims claims) {
            this.tokenData.claims = claims;
            return this;
        }
        public Builder authorities(List<? extends GrantedAuthority> authorities) {
            this.tokenData.grantedAuthorities = authorities;
            return this;
        }

        public Builder valid(boolean isValid) {
            this.tokenData.valid = isValid;
            return this;
        }

        public Builder expired(boolean isExpired) {
            this.tokenData.expired = isExpired;
            return this;
        }
        public TokenData build() {
            return this.tokenData;
        }
    }
}
