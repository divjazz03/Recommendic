package com.divjazz.recommendic.user.enums.converter;

import com.divjazz.recommendic.user.enums.Authority;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.Assert;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Authority, String> {

    @Override
    public String convertToDatabaseColumn(Authority permission) {
        if (permission == null)
            return null;
        return permission.getValue();
    }

    @Override
    public Authority convertToEntityAttribute(String code) {
        if (code ==  null)
            return null;
        return Stream
                .of(Authority.values())
                .filter(authority -> authority.getValue().equals(code))
                .findFirst()
                .orElseThrow(IllegalAccessError::new);
    }
}
