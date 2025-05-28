package com.divjazz.recommendic.user.model.userAttributes.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCredential {

    private String password;
    private final String expiry;

    @JsonProperty("last_modified")
    private String lastModified;

    public UserCredential(String password) {
        this.password = password;
        this.lastModified = LocalDateTime.now().toString();
        this.expiry = LocalDateTime.now().plusMonths(9).toString();
    }
    public UserCredential(String password, String lastModified, String expiry) {
        this.password = password;
        this.lastModified = LocalDateTime.now().toString();
        this.expiry = LocalDateTime.now().plusMonths(9).toString();
    }

    public String getExpiry() {
        return expiry;
    }

    public String getLastModified() {
        return lastModified;
    }

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }
    @JsonIgnore
    public LocalDateTime getLastModifiedLocalDateTime() {
        return LocalDateTime.parse(lastModified, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
    @JsonIgnore
    public void setLastModified(LocalDateTime last_modified) {
        this.lastModified = last_modified.toString();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(LocalDateTime.parse(expiry, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
