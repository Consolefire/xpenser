package com.consolefire.xpenser.store.data.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.consolefire.xpenser.store.PostgresTestcontainersTest;
import com.consolefire.xpenser.store.data.TaxUnit;
import com.consolefire.xpenser.store.data.UnitValue;
import com.consolefire.xpenser.store.data.dto.request.ItemRequestDto;
import com.consolefire.xpenser.store.data.dto.request.PaymentRequestDto;
import com.consolefire.xpenser.store.data.dto.request.ReceiptRequestDto;
import com.consolefire.xpenser.store.data.dto.request.TaxDetailRequestDto;
import com.consolefire.xpenser.store.data.dto.request.TaxRequestDto;
import com.consolefire.xpenser.store.data.dto.request.TaxUnitRequestDto;
import com.consolefire.xpenser.store.data.dto.request.UnitValueRequestDto;
import com.consolefire.xpenser.store.data.dto.response.ReceiptResponseDto;
import com.consolefire.xpenser.store.data.entity.ItemEntity;
import com.consolefire.xpenser.store.data.entity.PaymentEntity;
import com.consolefire.xpenser.store.data.entity.ReceiptEntity;
import com.consolefire.xpenser.store.data.entity.TaxDetailEntity;
import com.consolefire.xpenser.store.data.entity.TaxEntity;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("ReceiptMapper Spring Boot Integration Tests (with PostgreSQL Testcontainer)")
@SpringBootTest
class ReceiptMapperTest implements PostgresTestcontainersTest { // Implements the interface


    @Autowired
    private ReceiptMapper receiptMapper;

    /**
     * Helper to create a complete ReceiptRequestDto for testing using builders.
     */
    private ReceiptRequestDto createReceiptRequestDto() {
        return ReceiptRequestDto.builder()
            .identifier("MAP-REQ-001")
            .marketPlaceCode("MARKET-A")
            .storeCode("STORE-X")
            .timestamp(Instant.parse("2025-01-15T10:00:00Z"))
            .total(new BigDecimal("150.75"))
            .currency("EUR")
            .metadata(Map.of("source", "mobile", "promo", "SPRING_SALE"))
            .payment(PaymentRequestDto.builder()
                .amount(new BigDecimal("150.75"))
                .mode("CARD")
                .currency("EUR")
                .tax(TaxRequestDto.builder()
                    .total(new BigDecimal("25.00"))
                    .taxes(List.of(
                        TaxDetailRequestDto.builder()
                            .type("VAT")
                            .taxUnit(
                                TaxUnitRequestDto.builder().value(new BigDecimal("0.19")).unit("percentage").build())
                            .amount(new BigDecimal("19.00"))
                            .taxableAmount(new BigDecimal("100.00"))
                            .itemCategory("Electronics")
                            .build(),
                        TaxDetailRequestDto.builder()
                            .type("ECO_TAX")
                            .taxUnit(TaxUnitRequestDto.builder().value(new BigDecimal("1.00")).unit("fixed").build())
                            .amount(new BigDecimal("1.00"))
                            .taxableAmount(new BigDecimal("50.00"))
                            .itemCategory("Packaging")
                            .build()
                    ))
                    .build())
                .build())
            .items(List.of(
                ItemRequestDto.builder()
                    .code("ITEM-1")
                    .title("Laptop")
                    .quantity(1)
                    .price(new BigDecimal("100.00"))
                    .categoryCode("ELECTRONICS")
                    .unitValue(UnitValueRequestDto.builder().unitCode("PCS").value(new BigDecimal("1")).build())
                    .build(),
                ItemRequestDto.builder()
                    .code("ITEM-2")
                    .title("Mouse")
                    .quantity(2)
                    .price(new BigDecimal("25.00"))
                    .categoryCode("PERIPHERALS")
                    .unitValue(UnitValueRequestDto.builder().unitCode("PCS").value(new BigDecimal("1")).build())
                    .build()))
            .build();
    }

    /**
     * Helper to create a complete ReceiptEntity for testing using builders. Metadata is now a Map<String, String>
     * directly.
     */
    private ReceiptEntity createReceiptEntity() {
        TaxDetailEntity taxDetail = TaxDetailEntity.builder()
            .id(401L)
            .type("STATE_TAX")
            .taxUnit(TaxUnit.builder().value(new BigDecimal("0.075")).unit("percentage").build())
            .amount(new BigDecimal("15.00"))
            .taxableAmount(new BigDecimal("200.00"))
            .itemCategory("All")
            .build();

        TaxEntity tax = TaxEntity.builder()
            .id(301L)
            .total(new BigDecimal("15.00"))
            .taxDetails(List.of(taxDetail))
            .build();

        PaymentEntity payment = PaymentEntity.builder()
            .id(201L)
            .amount(new BigDecimal("200.50"))
            .mode("CREDIT_CARD")
            .currency("USD")
            .tax(tax)
            .build();

        ItemEntity item = ItemEntity.builder()
            .id(501L)
            .code("ITEM-A")
            .title("Monitor")
            .quantity(1)
            .price(new BigDecimal("200.00"))
            .categoryCode("ELECTRONICS")
            .unitValue(UnitValue.builder().unitCode("PCS").value(new BigDecimal("1")).build())
            .build();

        return ReceiptEntity.builder()
            .id(101L)
            .identifier("MAP-ENT-001")
            .marketPlaceCode("MARKET-B")
            .storeCode("STORE-Y")
            .generatedAt(Instant.parse("2025-02-20T12:30:00Z"))
            .total(new BigDecimal("200.50"))
            .currency("USD")
            .metadata(Map.of("client", "premium")) // Now directly a Map<String, String>
            .createdAt(Instant.parse("2025-02-20T12:00:00Z")) // Populate createdAt for the test
            .updatedAt(Instant.parse("2025-02-20T12:35:00Z"))
            .createdBy("system")
            .updatedBy("admin")
            .payment(payment)
            .items(List.of(item))
            .build();
    }


    @Test
    @DisplayName("Should map ReceiptRequestDto to ReceiptEntity correctly")
    void shouldMapRequestDtoToEntity() {
        // Given
        ReceiptRequestDto requestDto = createReceiptRequestDto();

        // When
        ReceiptEntity entity = receiptMapper.toEntity(requestDto);

        // Then - Assertions for scalar fields
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getIdentifier()).isEqualTo(requestDto.getIdentifier());
        assertThat(entity.getMarketPlaceCode()).isEqualTo(requestDto.getMarketPlaceCode());
        assertThat(entity.getStoreCode()).isEqualTo(requestDto.getStoreCode());
        assertThat(entity.getGeneratedAt()).isEqualTo(requestDto.getTimestamp());
        assertThat(entity.getTotal()).isEqualByComparingTo(requestDto.getTotal());
        assertThat(entity.getCurrency()).isEqualTo(requestDto.getCurrency());
        // Assert metadata directly as Map<String, String>
        assertThat(entity.getMetadata()).isEqualTo(requestDto.getMetadata());

        // Assertions for Auditable fields
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
        assertThat(entity.getCreatedBy()).isNull();
        assertThat(entity.getUpdatedBy()).isNull();

        // Assertions for nested PaymentEntity
        assertThat(entity.getPayment()).isNotNull();
        assertThat(entity.getPayment().getAmount()).isEqualByComparingTo(requestDto.getPayment().getAmount());
        assertThat(entity.getPayment().getMode()).isEqualTo(requestDto.getPayment().getMode());
        assertThat(entity.getPayment().getCurrency()).isEqualTo(requestDto.getPayment().getCurrency());
        assertThat(entity.getPayment().getReceipt()).isNotNull(); // Bidirectional link set by mapper

        // Assertions for nested TaxEntity
        assertThat(entity.getPayment().getTax()).isNotNull();
        assertThat(entity.getPayment().getTax().getTotal()).isEqualByComparingTo(
            requestDto.getPayment().getTax().getTotal());
        assertThat(entity.getPayment().getTax().getPayment()).isNull(); // Bidirectional link not set by mapper

        // Assertions for nested TaxDetailEntities
        assertThat(entity.getPayment().getTax().getTaxDetails()).hasSize(
            requestDto.getPayment().getTax().getTaxes().size());
        assertThat(entity.getPayment().getTax().getTaxDetails().get(0).getType()).isEqualTo(
            requestDto.getPayment().getTax().getTaxes().get(0).getType());
        assertThat(entity.getPayment().getTax().getTaxDetails().get(0).getTaxUnit().getValue()).isEqualByComparingTo(
            requestDto.getPayment().getTax().getTaxes().get(0).getTaxUnit().getValue());
        assertThat(entity.getPayment().getTax().getTaxDetails().get(0).getTaxUnit().getUnit()).isEqualTo(
            requestDto.getPayment().getTax().getTaxes().get(0).getTaxUnit().getUnit());
        assertThat(entity.getPayment().getTax().getTaxDetails().get(0)
            .getTax()).isNotNull(); // Bidirectional link  set by mapper

        // Assertions for nested ItemEntities
        assertThat(entity.getItems()).hasSize(requestDto.getItems().size());
        assertThat(entity.getItems().get(0).getTitle()).isEqualTo(requestDto.getItems().get(0).getTitle());
        assertThat(entity.getItems().get(0).getQuantity()).isEqualTo(requestDto.getItems().get(0).getQuantity());
        assertThat(entity.getItems().get(0).getReceipt()).isNotNull(); // Bidirectional link  set by mapper
    }

    @Test
    @DisplayName("Should map ReceiptEntity to ReceiptResponseDto correctly")
    void shouldMapEntityToResponseDto() {
        // Given
        ReceiptEntity entity = createReceiptEntity();

        // When
        ReceiptResponseDto responseDto = receiptMapper.toResponseDto(entity);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getReceiptId()).isEqualTo(entity.getId());
        assertThat(responseDto.getIdentifier()).isEqualTo(entity.getIdentifier());
        assertThat(responseDto.getCreatedAt()).isEqualTo(entity.getCreatedAt()); // Assert createdAt mapping
    }

    @Test
    @DisplayName("Should handle null ReceiptRequestDto gracefully")
    void shouldHandleNullRequestDto() {
        // When
        ReceiptEntity entity = receiptMapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should handle null ReceiptEntity gracefully for response mapping")
    void shouldHandleNullEntityForResponseDto() {
        // When
        ReceiptResponseDto responseDto = receiptMapper.toResponseDto(null);

        // Then
        assertThat(responseDto).isNull();
    }

    @Test
    @DisplayName("Should handle empty lists in DTO to Entity mapping")
    void shouldHandleEmptyListsInRequestDto() {
        // Given
        ReceiptRequestDto requestDto = ReceiptRequestDto.builder()
            .identifier("EMPTY-LISTS")
            .marketPlaceCode("MARKET-E")
            .storeCode("STORE-F")
            .timestamp(Instant.now())
            .total(BigDecimal.ZERO)
            .currency("USD")
            .metadata(Collections.emptyMap()) // Pass empty map for metadata
            .payment(PaymentRequestDto.builder()
                .amount(BigDecimal.ZERO)
                .mode("NONE")
                .currency("USD")
                .tax(TaxRequestDto.builder()
                    .total(BigDecimal.ZERO)
                    .taxes(Collections.emptyList()) // Pass empty list for taxes
                    .build())
                .build())
            .items(Collections.emptyList()) // Pass empty list for items
            .build();

        // When
        ReceiptEntity entity = receiptMapper.toEntity(requestDto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getItems()).isNotNull().isEmpty();
        assertThat(entity.getMetadata()).isNotNull().isEmpty();

        assertThat(entity.getPayment()).isNotNull();
        assertThat(entity.getPayment().getTax()).isNotNull();
        assertThat(entity.getPayment().getTax().getTaxDetails()).isNotNull().isEmpty();
    }
}