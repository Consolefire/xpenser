package com.consolefire.xpenser.expense.data.entiry.converter;

import com.consolefire.xpenser.expense.data.UnitValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;

@Converter
public class UnitValueAttributeConverter implements AttributeConverter<UnitValue, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(UnitValue attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting object to JSON string", e);
        }
    }

    @Override
    public UnitValue convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, UnitValue.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON string to object", e);
        }
    }
}
