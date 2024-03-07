package com.divjazz.recommendic.user.model.userAttributeConverter;

import com.divjazz.recommendic.user.model.userAttributes.PhoneNumber;
import org.springframework.core.convert.converter.Converter;

public class PhoneNumberConverter implements Converter<PhoneNumber, String> {
    @Override
    public String convert(PhoneNumber source) {
        return source.getPhoneNumber();
    }
}
