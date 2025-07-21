package com.consolefire.xpenser.store.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Getter
@ToString
@Jacksonized
@Builder
public final class TaxDetailRequestDto {

    @JsonProperty("type")
    private final String type;

    @JsonProperty("taxUnit")
    private final TaxUnitRequestDto taxUnit;

    @JsonProperty("taxableAmount")
    private final BigDecimal taxableAmount;

    @JsonProperty("amount")
    private final BigDecimal amount;

    @JsonProperty("itemCategory")
    private final String itemCategory;
}