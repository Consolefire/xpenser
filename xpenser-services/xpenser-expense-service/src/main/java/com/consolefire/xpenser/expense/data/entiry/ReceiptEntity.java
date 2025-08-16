package com.consolefire.xpenser.expense.data.entiry;

import static com.consolefire.xpenser.expense.data.entiry.EntityConstants.SEQ_DEFAULT_ALLOCATION_SIZE;
import static com.consolefire.xpenser.expense.data.entiry.EntityConstants.SEQ_NAME_RECEIPTS;
import static com.consolefire.xpenser.expense.data.entiry.EntityConstants.TABLE_NAME_RECEIPTS;

import com.consolefire.xpenser.expense.data.entiry.converter.StringMapConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
@Table(name = TABLE_NAME_RECEIPTS, uniqueConstraints = {
    @UniqueConstraint(
        columnNames = {"identifier", "market_place_code", "store_code"},
        name = "uk_market_store_identifier")})
public class ReceiptEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = SEQ_NAME_RECEIPTS, allocationSize =
        SEQ_DEFAULT_ALLOCATION_SIZE)
    private Long id;

    @Column(name = "identifier", nullable = false)
    @EqualsAndHashCode.Include
    private String identifier;

    @EqualsAndHashCode.Include
    @Column(name = "market_place_code", nullable = false)
    private String marketPlaceCode;

    @EqualsAndHashCode.Include
    @Column(name = "store_code", nullable = false)
    private String storeCode;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal total;

    @Column(nullable = false, length = 3)
    private String currency;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private PaymentEntity payment; // Added back as a separate entity

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEntity> items = new ArrayList<>();

    @Convert(converter = StringMapConverter.class) // Custom converter for Map<String,String>
    private Map<String, String> metadata; // Added back

    // Helper method to ensure bidirectional relationship consistency
    public void addItem(ItemEntity item) {
        if (item == null) {
            return;
        }
        if (!items.contains(item)) {
            items.add(item);
            item.setReceipt(this);
        }
    }

    public void removeItem(ItemEntity item) {
        if (item == null) {
            return;
        }
        if (items.contains(item)) {
            items.remove(item);
            item.setReceipt(null);
        }
    }

    // Helper to replace all items (useful for updates)
    public void setItems(List<ItemEntity> newItems) {
        this.items.clear(); // Clear existing items
        if (newItems != null) {
            newItems.forEach(this::addItem); // Add new items and set bidirectional link
        }
    }
}