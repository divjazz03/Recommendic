package com.divjazz.recommendic.user.model.userAttributes;

import com.google.common.base.MoreObjects;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class PhoneNumber {

    private String phoneNumber;

    public PhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    protected PhoneNumber(){}

    public String asString(){
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
