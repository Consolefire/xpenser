package com.consolefire.xpenser.expense.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
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
@Jacksonized
@Builder(toBuilder = true)
@ToString(exclude = "items")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ReceiptUpdateRequestDto {

    @JsonProperty("metadata")
    private final Map<String, String> metadata;

    @JsonProperty("total")
    private final BigDecimal total;

    @JsonProperty("currency")
    private final String currency;

    @JsonProperty("payment")
    private final PaymentRequestDto payment;

    @JsonProperty("items")
    private final List<ItemRequestDto> items;

}