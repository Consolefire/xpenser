package com.consolefire.xpenser.expenseimport.data.entity;

import static com.consolefire.xpenser.expenseimport.data.entity.EntityConstants.SEQ_DEFAULT_ALLOCATION_SIZE;
import static com.consolefire.xpenser.expenseimport.data.entity.EntityConstants.SEQ_TEMPLATES;
import static com.consolefire.xpenser.expenseimport.data.entity.EntityConstants.TABLE_NAME_TEMPLATES;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
@Table(name = TABLE_NAME_TEMPLATES)
public class Template extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(
        name = SEQ_TEMPLATES,
        allocationSize = SEQ_DEFAULT_ALLOCATION_SIZE)
    private Long id;

    @Column(name = "template_name", nullable = false, unique = true, updatable = false)
    @EqualsAndHashCode.Include
    private String name;

    @Lob
    @Column(name = "template_data", nullable = false)
    private String data;

}
