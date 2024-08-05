package com.divjazz.recommendic.security.domain;

public class Token {
    private String access;
    private String refresh;

    private Token(){}

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

    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        private final Token token;


        private Builder(){
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
        public Token build(){
            return this.token;
        }
    }
}
