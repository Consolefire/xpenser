package com.consolefire.tools.mdm.data.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "properties")
public class Property {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "key", unique = true, nullable = false)
    private String key;
    @Column(name = "value")
    private String value;
    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "data_type_code", referencedColumnName = "code")
    private DataType dataType;
    @Column(name = "category_code", nullable = false)
    private Category category;
    @Column(name = "is_enabled")
    private boolean enabled = true;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

}
