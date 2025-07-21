package com.divjazz.recommendic.user.model.userAttributes.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCredential implements Serializable {

    private String password;
    private String expiry;
    @Setter
    private String lastModified;

    public UserCredential(String password) {
        this.password = password;
        this.lastModified = LocalDateTime.now().toString();
        this.expiry = LocalDateTime.now().plusMonths(9).toString();
    }
    public UserCredential(String password, String expiry, String lastModified){
        this.password = password;
        this.lastModified = lastModified;
        this.expiry = expiry;
    }
    @JsonIgnore
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(LocalDateTime.parse(expiry, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
