package com.consolefire.tools.mdm.data.converter;

import com.consolefire.tools.mdm.data.entity.DataTypeMetadata;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Converter;

@Converter
public class DataTypeMetadataAttributeConverter extends JsonAttributeConverter<DataTypeMetadata> {

    @Override
    protected TypeReference<DataTypeMetadata> targetTypeReference() {
        return new TypeReference<DataTypeMetadata>() {
        };
    }
}
