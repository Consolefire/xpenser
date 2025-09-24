package com.consolefire.xpenser.doc.data.entity;

import static com.consolefire.xpenser.doc.data.entity.EntityConstants.SEQ_DEFAULT_ALLOCATION_SIZE;
import static com.consolefire.xpenser.doc.data.entity.EntityConstants.SEQ_RECEIPT_DOCUMENTS;
import static com.consolefire.xpenser.doc.data.entity.EntityConstants.TABLE_NAME_RECEIPT_DOCUMENTS;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = TABLE_NAME_RECEIPT_DOCUMENTS, uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_receipt_file_id",
        columnNames = {"receipt_id", "file_id"}
    )
})
public class ReceiptDocumentEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(
        name = SEQ_RECEIPT_DOCUMENTS,
        allocationSize = SEQ_DEFAULT_ALLOCATION_SIZE)
    private Long id;

    @Column(name = "receipt_id", nullable = false)
    @EqualsAndHashCode.Include
    private String receiptId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    @EqualsAndHashCode.Include
    private FileEntity receiptFile;

}
