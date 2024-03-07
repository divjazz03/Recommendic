package com.divjazz.recommendic.user.model.userAttributes;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class PhoneNumber {

    private String phoneNumber;

    public PhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    protected PhoneNumber(){}

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return MoreObjects
                .toStringHelper(this)
                .add("phoneNumber", phoneNumber)
                .toString();
    }
}
