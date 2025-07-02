package com.divjazz.recommendic.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.ZoneOffset;

@Converter(autoApply = true)
public class ZoneOffsetConverter implements AttributeConverter<ZoneOffset, String> {
    @Override
    public String convertToDatabaseColumn(ZoneOffset attribute) {

        return attribute != null ? attribute.getId() : null;
    }

    @Override
    public ZoneOffset convertToEntityAttribute(String dbData) {
        return dbData != null ? ZoneOffset.of(dbData) : null;
    }
}
