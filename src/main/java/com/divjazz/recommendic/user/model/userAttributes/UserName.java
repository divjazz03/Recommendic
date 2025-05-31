package com.divjazz.recommendic.user.model.userAttributes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserName {


    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;


    protected UserName() {
    }

    public UserName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @JsonProperty("full_name")
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }
}
