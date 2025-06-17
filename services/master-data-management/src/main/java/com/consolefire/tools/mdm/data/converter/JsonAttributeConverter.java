package com.consolefire.tools.mdm.data.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public abstract class JsonAttributeConverter<X> implements AttributeConverter<X, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(X attribute) {
        if (null == attribute) {
            log.debug("Given attribute is NULL");
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public X convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            log.debug("DB data is null or empty");
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(dbData, targetTypeReference());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract TypeReference<X> targetTypeReference();
}
