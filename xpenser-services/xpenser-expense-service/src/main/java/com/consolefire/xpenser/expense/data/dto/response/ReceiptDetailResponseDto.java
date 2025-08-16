package com.consolefire.xpenser.expense.data.dto.response;

import com.consolefire.xpenser.expense.data.dto.request.ItemRequestDto;
import com.consolefire.xpenser.expense.data.dto.request.PaymentRequestDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@With
@Getter
@Jacksonized
@ToString
@Builder(toBuilder = true)
public class ReceiptDetailResponseDto {

    @JsonProperty("receiptId")
    @EqualsAndHashCode.Include
    private final Long receiptId;

    @JsonProperty("identifier")
    @EqualsAndHashCode.Include
    private final String identifier;

    @JsonProperty("metadata")
    private final Map<String, String> metadata;

    @JsonProperty("marketPlaceCode")
    @EqualsAndHashCode.Include
    private final String marketPlaceCode;

    @JsonProperty("storeCode")
    @EqualsAndHashCode.Include
    private final String storeCode;

    @JsonProperty("generatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant generatedAt;

    @JsonProperty("total")
    private final BigDecimal total;

    @JsonProperty("currency")
    private final String currency;

    @JsonProperty("payment")
    private final PaymentRequestDto payment;

    @JsonProperty("items")
    private final List<ItemRequestDto> items;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant createdAt;
    @JsonProperty("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant updatedAt;
    @JsonProperty("createdBy")
    private final String createdBy;
    @JsonProperty("updatedBy")
    private final String updatedBy;
}
