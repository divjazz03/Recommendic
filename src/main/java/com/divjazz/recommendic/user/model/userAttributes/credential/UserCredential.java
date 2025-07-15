package com.divjazz.recommendic.user.model.userAttributes.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class UserCredential {

    private final String password;
    private final String expiry;
    @Setter
    private String lastModified;

    public UserCredential(String password) {
        this.password = password;
        this.lastModified = LocalDateTime.now().toString();
        this.expiry = LocalDateTime.now().plusMonths(9).toString();
    }
    @JsonIgnore
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(LocalDateTime.parse(expiry, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
