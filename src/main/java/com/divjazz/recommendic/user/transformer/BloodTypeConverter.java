package com.divjazz.recommendic.user.transformer;


import com.divjazz.recommendic.user.enums.BloodType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@Converter
public class BloodTypeConverter implements AttributeConverter<BloodType, String> {

    @Override
    public String convertToDatabaseColumn(BloodType attribute) {
        return attribute.getValue();
    }

    @Override
    public BloodType convertToEntityAttribute(String dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }
        return BloodType.fromValue(dbData);
    }
}
