package com.consolefire.xpenser.expense.data.entiry.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.convert.converter.Converter;

public class ObjectToJsonStringConverter<S> implements Converter<S, String> {

    private final ObjectMapper objectMapper;

    public ObjectToJsonStringConverter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String convert(S source) {
        if (null == source) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(source);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert", e);
        }
    }

}
