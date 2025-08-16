package com.consolefire.xpenser.expense.data.entiry.converter;

import com.consolefire.xpenser.expense.data.TaxUnit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;

@Converter
public class TaxUnitAttributeConverter implements AttributeConverter<TaxUnit, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(TaxUnit attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting Map to JSON string", e);
        }
    }

    @Override
    public TaxUnit convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, TaxUnit.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON string to Map", e);
        }
    }
}
