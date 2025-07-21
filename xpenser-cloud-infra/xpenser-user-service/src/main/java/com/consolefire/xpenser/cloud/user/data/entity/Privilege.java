package com.consolefire.xpenser.cloud.user.data.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Table("privileges") // R2DBC table annotation
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
public class Privilege {
    @Id // R2DBC ID annotation
    private Long id;

    private String name;

    public Privilege(String name) {
        this.name = name;
    }
}

