package com.consolefire.xpenser.store.data.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal; // Use BigDecimal for monetary values
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "payments")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaymentEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, precision = 19, scale = 4) // Example precision/scale for BigDecimal
    private BigDecimal amount;

    @Column(nullable = false)
    private String mode;

    @Column(nullable = false, length = 3)
    private String currency;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tax_id", referencedColumnName = "id")
    @ToString.Exclude
    private TaxEntity tax;

    // Optional: Bidirectional link back to Receipt if needed for navigation from Payment
    @OneToOne(mappedBy = "payment")
    @ToString.Exclude
    private ReceiptEntity receipt;
}