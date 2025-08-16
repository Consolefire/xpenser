package com.consolefire.xpenser.expense.data.entiry;

import static com.consolefire.xpenser.expense.data.entiry.EntityConstants.SEQ_DEFAULT_ALLOCATION_SIZE;
import static com.consolefire.xpenser.expense.data.entiry.EntityConstants.SEQ_NAME_ITEMS;
import static com.consolefire.xpenser.expense.data.entiry.EntityConstants.TABLE_NAME_ITEMS;

import com.consolefire.xpenser.expense.data.UnitValue;
import com.consolefire.xpenser.expense.data.entiry.converter.UnitValueAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = TABLE_NAME_ITEMS, uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_item_code_receipt_id",
        columnNames = {"code", "receipt_id"}
    )
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = SEQ_NAME_ITEMS, allocationSize =
        SEQ_DEFAULT_ALLOCATION_SIZE)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer quantity; // Quantity from DTO

    @Column(nullable = false, precision = 19, scale = 4) // Using BigDecimal for price
    private BigDecimal price;

    @Column(nullable = false)
    private String categoryCode;

    @Column(name = "unit_value")
    @Convert(converter = UnitValueAttributeConverter.class)
    private UnitValue unitValue;

    @ManyToOne
    @JoinColumn(name = "receipt_id", nullable = false)
    @ToString.Exclude
    private ReceiptEntity receipt;

}