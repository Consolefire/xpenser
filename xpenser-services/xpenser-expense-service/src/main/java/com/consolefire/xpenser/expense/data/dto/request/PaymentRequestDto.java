package com.consolefire.xpenser.expense.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@Jacksonized
@Builder
public final class PaymentRequestDto {

    @JsonProperty("amount")
    private final BigDecimal amount;

    @JsonProperty("mode")
    private final String mode;

    @JsonProperty("currency")
    private final String currency;

    @JsonProperty("tax")
    private final TaxRequestDto tax;
}