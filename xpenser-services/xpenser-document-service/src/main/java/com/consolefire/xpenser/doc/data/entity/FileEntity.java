package com.consolefire.xpenser.doc.data.entity;

import static com.consolefire.xpenser.doc.data.entity.EntityConstants.SEQ_DEFAULT_ALLOCATION_SIZE;
import static com.consolefire.xpenser.doc.data.entity.EntityConstants.SEQ_FILES;
import static com.consolefire.xpenser.doc.data.entity.EntityConstants.TABLE_NAME_FILES;

import com.consolefire.xpenser.doc.data.converter.UuidStringAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants(asEnum = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
@Table(name = TABLE_NAME_FILES)
public class FileEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(
        name = SEQ_FILES,
        allocationSize = SEQ_DEFAULT_ALLOCATION_SIZE)
    private Long id;

    @Column(name = "identifier", nullable = false)
    @EqualsAndHashCode.Include
    @Convert(converter = UuidStringAttributeConverter.class)
    private UUID fileId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "checksum", nullable = false)
    private String checksum;

}
