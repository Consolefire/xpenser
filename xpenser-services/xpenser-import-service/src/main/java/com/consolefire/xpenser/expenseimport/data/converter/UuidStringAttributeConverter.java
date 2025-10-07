package com.consolefire.xpenser.expenseimport.data.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

@Converter
public class UuidStringAttributeConverter implements AttributeConverter<UUID, String> {

    @Override
    public String convertToDatabaseColumn(UUID attribute) {
        if (null == attribute) {
            return null;
        }
        return attribute.toString();
    }

    @Override
    public UUID convertToEntityAttribute(String dbData) {
        if (null == dbData || dbData.isBlank()) {
            return null;
        }
        return UUID.fromString(dbData);
    }
}
