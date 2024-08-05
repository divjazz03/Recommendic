package com.divjazz.recommendic.security.domain;

import com.divjazz.recommendic.user.model.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class TokenData {
    private User user;
    private Claims claims;
    private boolean valid;
    private List<? extends GrantedAuthority> grantedAuthorities;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Claims getClaims() {
        return claims;
    }

    public void setClaims(Claims claims) {
        this.claims = claims;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<? extends GrantedAuthority> getGrantedAuthorities() {
        return grantedAuthorities;
    }

    public void setGrantedAuthorities(List<? extends GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TokenData tokenData;

        private Builder() {
            this.tokenData = new TokenData();
        }
        public Builder user(User user) {
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
        public TokenData build() {
            return this.tokenData;
        }
    }
}
