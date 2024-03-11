package com.divjazz.recommendic.user.model.userAttributeConverter;

import com.divjazz.recommendic.user.model.userAttributes.PhoneNumber;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PhoneNumberConverter implements AttributeConverter<PhoneNumber, String> {

    @Override
    public String convertToDatabaseColumn(PhoneNumber phoneNumber) {
        return phoneNumber.asString();
    }

    @Override
    public PhoneNumber convertToEntityAttribute(String s) {
        return null;
    }
}
