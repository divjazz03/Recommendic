package com.divjazz.recommendic.user.model.userAttributes;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class UserName {

    private String firstName;
    private String lastName;


    protected UserName(){}

    public UserName(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }
}
