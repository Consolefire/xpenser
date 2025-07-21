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
public final class TaxUnitRequestDto {

    @JsonProperty("value")
    private final BigDecimal value;

    @JsonProperty("unit")
    private final String unit;
}