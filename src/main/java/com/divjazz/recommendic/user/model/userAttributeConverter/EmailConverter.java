package com.divjazz.recommendic.user.model.userAttributeConverter;

import com.divjazz.recommendic.user.model.userAttributes.Email;
import org.springframework.core.convert.converter.Converter;

public class EmailConverter implements Converter<Email, String> {
    @Override
    public String convert(Email source) {
        return source.asString();
    }
}
