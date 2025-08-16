package com.consolefire.xpenser.store.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.consolefire.xpenser.store.PostgresTestcontainersTest;
import com.consolefire.xpenser.store.data.TaxUnit;
import com.consolefire.xpenser.store.data.UnitValue;
import com.consolefire.xpenser.store.data.entity.ItemEntity;
import com.consolefire.xpenser.store.data.entity.PaymentEntity;
import com.consolefire.xpenser.store.data.entity.ReceiptEntity;
import com.consolefire.xpenser.store.data.entity.TaxDetailEntity;
import com.consolefire.xpenser.store.data.entity.TaxEntity;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

// Implement the new interface
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@DisplayName("ReceiptRepository Integration Tests with Testcontainers")
class ReceiptRepositoryTest implements PostgresTestcontainersTest { // Implement the interface

    // Removed @Container and @DynamicPropertySource as they are now in PostgresTestcontainers interface

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReceiptRepository receiptRepository;

    private ReceiptEntity createTestReceipt(String identifier, String marketPlaceCode, String storeCode) {
        ReceiptEntity receipt = new ReceiptEntity();
        receipt.setIdentifier(identifier);
        receipt.setMarketPlaceCode(marketPlaceCode);
        receipt.setStoreCode(storeCode);
        receipt.setGeneratedAt(Instant.now());
        receipt.setTotal(new BigDecimal("100.00"));
        receipt.setCurrency("EUR");
        receipt.setMetadata(Map.of("source", "web", "type", "invoice"));

        receipt.setCreatedAt(Instant.now());
        receipt.setUpdatedAt(Instant.now());
        receipt.setCreatedBy("test_user");
        receipt.setUpdatedBy("test_user");

        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(new BigDecimal("100.00"));
        payment.setMode("CARD");
        payment.setCurrency("EUR");
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());
        payment.setCreatedBy("test_user");
        payment.setUpdatedBy("test_user");
        receipt.setPayment(payment);
        payment.setReceipt(receipt);

        TaxEntity tax = new TaxEntity();
        tax.setTotal(new BigDecimal("10.00"));
        tax.setCreatedAt(Instant.now());
        tax.setUpdatedAt(Instant.now());
        tax.setCreatedBy("test_user");
        tax.setUpdatedBy("test_user");

        TaxDetailEntity taxDetail1 = new TaxDetailEntity();
        taxDetail1.setType("VAT");
        taxDetail1.setTaxUnit(new TaxUnit(new BigDecimal("0.10"), "percentage"));
        taxDetail1.setAmount(new BigDecimal("10.00"));
        taxDetail1.setTaxableAmount(new BigDecimal("100.00"));
        taxDetail1.setItemCategory("General");
        taxDetail1.setCreatedAt(Instant.now());
        taxDetail1.setUpdatedAt(Instant.now());
        taxDetail1.setCreatedBy("test_user");
        taxDetail1.setUpdatedBy("test_user");
        tax.addTaxDetail(taxDetail1);

        payment.setTax(tax);
        tax.setPayment(payment);

        ItemEntity item1 = new ItemEntity();
        item1.setCode("PROD001");
        item1.setTitle("Product A");
        item1.setQuantity(1);
        item1.setPrice(new BigDecimal("60.00"));
        item1.setCategoryCode("ELECTRONICS");
        item1.setUnitValue(UnitValue.builder().unitCode("PSC").value(new BigDecimal("1")).build());
        item1.setCreatedAt(Instant.now());
        item1.setUpdatedAt(Instant.now());
        item1.setCreatedBy("test_user");
        item1.setUpdatedBy("test_user");
        receipt.addItem(item1);

        ItemEntity item2 = new ItemEntity();
        item2.setCode("PROD002");
        item2.setTitle("Product B");
        item2.setQuantity(2);
        item2.setPrice(new BigDecimal("20.00"));
        item2.setCategoryCode("ELECTRONICS");
        item2.setUnitValue(UnitValue.builder().unitCode("PSC").value(new BigDecimal("1")).build());
        item2.setCreatedAt(Instant.now());
        item2.setUpdatedAt(Instant.now());
        item2.setCreatedBy("test_user");
        item2.setUpdatedBy("test_user");
        receipt.addItem(item2);

        return receipt;
    }

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager().createNativeQuery("TRUNCATE TABLE receipts CASCADE;").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Should find a receipt by its unique key")
    void shouldFindByUniqueKey() {
        ReceiptEntity receiptToPersist = createTestReceipt("TEST-ID-1", "MARKET-A", "STORE-X");
        ReceiptEntity persistedReceipt = entityManager.persistAndFlush(receiptToPersist);

        Optional<ReceiptEntity> foundReceipt = receiptRepository.findByUniqueKey(
            "TEST-ID-1", "MARKET-A", "STORE-X"
        );

        assertThat(foundReceipt).isPresent();
        assertThat(foundReceipt.get().getIdentifier()).isEqualTo("TEST-ID-1");
        assertThat(foundReceipt.get().getMarketPlaceCode()).isEqualTo("MARKET-A");
        assertThat(foundReceipt.get().getStoreCode()).isEqualTo("STORE-X");
        assertThat(foundReceipt.get().getId()).isEqualTo(persistedReceipt.getId());
    }

    @Test
    @DisplayName("Should return empty optional if unique key does not exist")
    void shouldNotFindByUniqueKeyIfNotFound() {
        Optional<ReceiptEntity> foundReceipt = receiptRepository.findByUniqueKey(
            "NON-EXISTENT", "MARKET-A", "STORE-X"
        );
        assertThat(foundReceipt).isNotPresent();
    }

    @Test
    @DisplayName("Should save a new receipt")
    void shouldSaveReceipt() {
        ReceiptEntity newReceipt = createTestReceipt("NEW-ID-2", "MARKET-B", "STORE-Y");
        ReceiptEntity savedReceipt = receiptRepository.save(newReceipt);
        entityManager.flush();

        assertThat(savedReceipt).isNotNull();
        assertThat(savedReceipt.getId()).isNotNull();
        assertThat(savedReceipt.getIdentifier()).isEqualTo("NEW-ID-2");

        Optional<ReceiptEntity> found = receiptRepository.findById(savedReceipt.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getIdentifier()).isEqualTo("NEW-ID-2");
    }

    @Test
    @DisplayName("Should find receipt by ID")
    void shouldFindById() {
        ReceiptEntity receiptToPersist = createTestReceipt("FIND-BY-ID-1", "MARKET-F", "STORE-Z");
        ReceiptEntity persistedReceipt = entityManager.persistAndFlush(receiptToPersist);

        Optional<ReceiptEntity> foundReceipt = receiptRepository.findById(persistedReceipt.getId());

        assertThat(foundReceipt).isPresent();
        assertThat(foundReceipt.get().getIdentifier()).isEqualTo("FIND-BY-ID-1");
    }

    @Test
    @DisplayName("Should return empty optional when finding non-existent ID")
    void shouldNotFindByIdIfNotFound() {
        Optional<ReceiptEntity> foundReceipt = receiptRepository.findById(9999L);
        assertThat(foundReceipt).isNotPresent();
    }

    @Test
    @Transactional
    @DisplayName("Should find all receipts")
    void shouldFindAllReceipts() {
        receiptRepository.saveAndFlush(createTestReceipt("TEST-ID-A", "MARKET-A", "STORE-1"));
        receiptRepository.saveAndFlush(createTestReceipt("TEST-ID-B", "MARKET-B", "STORE-2"));
        receiptRepository.saveAndFlush(createTestReceipt("TEST-ID-C", "MARKET-C", "STORE-3"));

        List<ReceiptEntity> receipts = receiptRepository.findAll();

        assertThat(receipts).isNotNull();
        assertThat(receipts).hasSize(3);
        assertThat(receipts).extracting(ReceiptEntity::getIdentifier)
            .containsExactlyInAnyOrder("TEST-ID-A", "TEST-ID-B", "TEST-ID-C");
    }

    @Test
    @DisplayName("Should delete a receipt")
    void shouldDeleteReceipt() {
        ReceiptEntity receiptToDelete = createTestReceipt("DELETE-ME", "MARKET-X", "STORE-Y");
        ReceiptEntity persistedReceipt = entityManager.persistAndFlush(receiptToDelete);

        receiptRepository.delete(persistedReceipt);
        entityManager.flush();

        Optional<ReceiptEntity> foundReceipt = receiptRepository.findById(persistedReceipt.getId());
        assertThat(foundReceipt).isNotPresent();
    }
}