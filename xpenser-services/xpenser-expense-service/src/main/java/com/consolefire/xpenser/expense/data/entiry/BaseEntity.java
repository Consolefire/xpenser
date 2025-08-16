package com.consolefire.xpenser.expense.data.entiry;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass // This tells JPA to include the fields of this class in subclasses' tables
public abstract class BaseEntity {

    @Version
    @Column(name = "record_version", insertable = false, updatable = false)
    private int recordVersion;

}
