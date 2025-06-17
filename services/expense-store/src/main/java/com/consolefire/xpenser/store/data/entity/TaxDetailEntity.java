package com.consolefire.xpenser.store.data.entity;

import com.consolefire.xpenser.store.data.TaxUnit;
import com.consolefire.xpenser.store.data.entity.converter.TaxUnitAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tax_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TaxDetailEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(name = "tax_unit")
    @Convert(converter = TaxUnitAttributeConverter.class)
    private TaxUnit taxUnit;

    @Column(name = "taxable_amount", nullable = false, precision = 19, scale = 4) // NEW FIELD
    private BigDecimal taxableAmount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "item_category", nullable = false)
    private String itemCategory;

    @ManyToOne
    @JoinColumn(name = "tax_id", nullable = false)
    @ToString.Exclude
    private TaxEntity tax;
}