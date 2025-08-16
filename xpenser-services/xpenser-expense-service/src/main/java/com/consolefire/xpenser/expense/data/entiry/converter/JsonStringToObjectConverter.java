package com.consolefire.xpenser.expense.data.entiry.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.lang.reflect.Type;
import org.springframework.core.convert.converter.Converter;

public class JsonStringToObjectConverter<T> implements Converter<String, T> {

    private final ObjectMapper objectMapper;
    private final Class<T> type;

    public JsonStringToObjectConverter(Class<T> type) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.type = type;
    }

    @Override
    public T convert(String source) {
        if (null == source || source.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(source, new TypeReference<>() {
                @Override
                public Type getType() {
                    return type;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert", e);
        }
    }

}
