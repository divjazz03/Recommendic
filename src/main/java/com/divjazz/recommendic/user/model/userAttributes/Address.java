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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
