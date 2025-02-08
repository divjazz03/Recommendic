package com.divjazz.recommendic.user.model.userAttributes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserName {


    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
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

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }
}
