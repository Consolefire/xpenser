package com.consolefire.xpenser.store.data.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Data Transfer Object for creating or updating a receipt. The combination of {@code identifier},
 * {@code marketPlaceCode}, and {@code storeCode} is intended to be unique at the persistence layer (database).
 */
@Getter
@ToString(exclude = "items")
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ReceiptRequestDto {

    @NotBlank // Example: Ensure identifier is not null or empty
    @JsonProperty("identifier")
    @EqualsAndHashCode.Include
    private final String identifier;

    @JsonProperty("metadata")
    private final Map<String, String> metadata;

    @NotBlank // Example: Ensure marketPlaceCode is not null or empty
    @JsonProperty("marketPlaceCode")
    @EqualsAndHashCode.Include
    private final String marketPlaceCode;

    @NotBlank // Example: Ensure storeCode is not null or empty
    @JsonProperty("storeCode")
    @EqualsAndHashCode.Include
    private final String storeCode;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant timestamp;

    @JsonProperty("total")
    private final BigDecimal total;

    @JsonProperty("currency")
    private final String currency;

    @JsonProperty("payment")
    private final PaymentRequestDto payment;

    @JsonProperty("items")
    private final List<ItemRequestDto> items;

    // Custom constructor for builder to ensure collection immutability
    ReceiptRequestDto(String identifier, Map<String, String> metadata, String marketPlaceCode, String storeCode,
        Instant timestamp, BigDecimal total, String currency, PaymentRequestDto payment,
        List<ItemRequestDto> items) {
        this.identifier = identifier;
        this.metadata = metadata != null ? Collections.unmodifiableMap(metadata) : Collections.emptyMap();
        this.marketPlaceCode = marketPlaceCode;
        this.storeCode = storeCode;
        this.timestamp = timestamp;
        this.total = total;
        this.currency = currency;
        this.payment = payment;
        this.items = items != null ? Collections.unmodifiableList(items) : Collections.emptyList();
    }

    // Builder methods to ensure defensive copy when building
    public static class ReceiptRequestDtoBuilder {

        public ReceiptRequestDtoBuilder metadata(Map<String, String> metadata) {
            this.metadata = metadata != null ? Collections.unmodifiableMap(metadata) : Collections.emptyMap();
            return this;
        }

        public ReceiptRequestDtoBuilder items(List<ItemRequestDto> items) {
            this.items = items != null ? Collections.unmodifiableList(items) : Collections.emptyList();
            return this;
        }
    }
}