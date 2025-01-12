package com.divjazz.recommendic.user.model.userAttributes;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;

@Embeddable
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Address {
    @JsonProperty(value = "zip_code")
    private String zipCode;
    private String city;
    private String State;
    private String Country;

    protected Address() {
    }

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

    @Override
    public String toString() {
        return "Address{" +
                "zipCode='" + zipCode + '\'' +
                ", city='" + city + '\'' +
                ", State='" + State + '\'' +
                ", Country='" + Country + '\'' +
                '}';
    }
}
