package com.divjazz.recommendic.user.model.userAttributes;


import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    private String zipCode;
    private String city;
    private String State;
    private String Country;

    protected Address(){}

    public Address(String zipCode, String city, String state, String country) {
        this.zipCode = zipCode;
        this.city = city;
        State = state;
        Country = country;
    }
}
