package com.divjazz.recommendic.user.model.userAttributeConverter;

import com.divjazz.recommendic.user.model.userAttributes.Email;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmailConverter implements AttributeConverter<Email, String> {

    @Override
    public String convertToDatabaseColumn(Email email) {
        return email.asString();
    }

    @Override
    public Email convertToEntityAttribute(String s) {
        return new Email(s);
    }
}
