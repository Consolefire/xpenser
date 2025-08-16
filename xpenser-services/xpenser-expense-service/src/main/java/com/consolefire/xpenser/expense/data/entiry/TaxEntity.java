package com.consolefire.xpenser.expense.data.entiry;

import static com.consolefire.xpenser.expense.data.entiry.EntityConstants.SEQ_DEFAULT_ALLOCATION_SIZE;
import static com.consolefire.xpenser.expense.data.entiry.EntityConstants.SEQ_NAME_TAXES;
import static com.consolefire.xpenser.expense.data.entiry.EntityConstants.TABLE_NAME_TAXES;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = TABLE_NAME_TAXES)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TaxEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = SEQ_NAME_TAXES, allocationSize =
        SEQ_DEFAULT_ALLOCATION_SIZE)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal total;

    @OneToMany(mappedBy = "tax", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaxDetailEntity> taxDetails = new ArrayList<>(); // Renamed for clarity

    // Optional: Bidirectional link back to Payment
    @OneToOne(mappedBy = "tax")
    @ToString.Exclude
    private PaymentEntity payment;

    // Helper methods for bidirectional relationship consistency
    public void addTaxDetail(TaxDetailEntity taxDetail) {
        if (taxDetail == null) {
            return;
        }
        if (!this.taxDetails.contains(taxDetail)) {
            this.taxDetails.add(taxDetail);
            taxDetail.setTax(this);
        }
    }

    public void removeTaxDetail(TaxDetailEntity taxDetail) {
        if (taxDetail == null) {
            return;
        }
        if (this.taxDetails.contains(taxDetail)) {
            this.taxDetails.remove(taxDetail);
            taxDetail.setTax(null);
        }
    }

    public void setTaxDetails(List<TaxDetailEntity> newTaxDetails) {
        this.taxDetails.clear();
        if (newTaxDetails != null) {
            newTaxDetails.forEach(this::addTaxDetail);
        }
    }
}