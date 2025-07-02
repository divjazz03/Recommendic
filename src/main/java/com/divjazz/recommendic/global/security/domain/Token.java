package com.divjazz.recommendic.global.security.domain;

public class Token {
    private String access;
    private String refresh;

    private Token() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAccess() {
        return access;
    }

    private void setAccess(String access) {
        this.access = access;
    }

    public String getRefresh() {
        return refresh;
    }

    private void setRefresh(String refresh) {
        this.refresh = refresh;
    }

    public static class Builder {
        private final Token token;


        private Builder() {
            token = new Token();
        }

        public Builder access(String token) {
            this.token.setAccess(token);
            return this;
        }

        public Builder refresh(String token) {
            this.token.setRefresh(token);
            return this;
        }

        public Token build() {
            return this.token;
        }
    }
}
