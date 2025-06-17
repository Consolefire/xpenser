package com.consolefire.tools.mdm.data.entity;

import com.consolefire.tools.mdm.data.converter.DataTypeMetadataAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "data_type")
public class DataType {

    @Id
    @Column(name = "code", unique = true, nullable = false)
    private String code;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "metadata")
    @Convert(converter = DataTypeMetadataAttributeConverter.class)
    private DataTypeMetadata metadata;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

}
