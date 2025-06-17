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
public final class ItemRequestDto {

    @JsonProperty("code")
    private final String code;

    @JsonProperty("title")
    private final String title;

    @JsonProperty("quantity")
    private final Integer quantity;

    @JsonProperty("price")
    private final BigDecimal price;

    @JsonProperty("categoryCode")
    private final String categoryCode;

    @JsonProperty("unitValue")
    private final UnitValueRequestDto unitValue;
}