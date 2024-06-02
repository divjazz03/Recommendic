package com.divjazz.recommendic.search;

import com.divjazz.recommendic.user.model.userAttributes.Address;

import java.util.Objects;

public final class ConsultantSearchResult extends SearchResult {
    String username;
    String email;
    String phoneNumber;
    String gender;
    Address address;
    String medicalExpertise;

    public ConsultantSearchResult(String username, String email, String phoneNumber, String gender, Address address, String medicalExpertise) {
        super();
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.address = address;
        this.medicalExpertise = medicalExpertise;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConsultantSearchResult that = (ConsultantSearchResult) o;

        if (!Objects.equals(username, that.username)) return false;
        if (!Objects.equals(email, that.email)) return false;
        if (!Objects.equals(phoneNumber, that.phoneNumber)) return false;
        if (!Objects.equals(gender, that.gender)) return false;
        if (!Objects.equals(address, that.address)) return false;
        return Objects.equals(medicalExpertise, that.medicalExpertise);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (medicalExpertise != null ? medicalExpertise.hashCode() : 0);
        return result;
    }

    public String getMedicalExpertise() {
        return medicalExpertise;
    }
}
