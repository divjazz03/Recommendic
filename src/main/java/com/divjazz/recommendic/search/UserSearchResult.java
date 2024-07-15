package com.divjazz.recommendic.search;

import com.divjazz.recommendic.user.model.userAttributes.Address;

public sealed class UserSearchResult permits ConsultantSearchResult {
    String username;
    String email;
    String phoneNumber;
    String gender;
    Address address;

    public UserSearchResult(String username, String email, String phoneNumber, String gender, Address address) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.address = address;
    }
}
